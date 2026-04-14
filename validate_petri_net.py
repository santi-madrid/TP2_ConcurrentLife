#!/usr/bin/env python3
"""
Validador de Invariantes para Red de Petri

Script para validar y contabilizar invariantes de transición en una Red de Petri
procesando un archivo de log de transiciones. Implementa búsqueda recursiva optimizada
usando expresiones regulares con clasificación automática de invariantes.

Invariantes válidos:
  1. T0 T1 T2 T5 T6 T9 T10 T11
  2. T0 T1 T2 T5 T7 T8 T11
  3. T0 T1 T3 T4 T6 T9 T10 T11
  4. T0 T1 T3 T4 T7 T8 T11
"""

import re
import logging
from pathlib import Path
from typing import Dict
from dataclasses import dataclass, field
from collections import Counter


@dataclass
class ValidationResult:
    """Resultado de la validación de invariantes."""

    is_valid: bool
    total_invariants: int
    invariant_counts: Dict[str, int] = field(default_factory=dict)
    remaining_transitions: str = ""
    error_message: str = ""


class PetriNetValidator:
    """
    Validador de invariantes para Redes de Petri.

    Procesa un archivo de log de transiciones y valida que siga los patrones
    de invariantes definidos, usando búsqueda recursiva iterativa con conteo
    de invariantes en cada pasada.
    """

    # Expresión regular para identificar invariantes
    # Estructura: T0 T1 (T2 T5 | T3 T4) (T7 T8 | T6 T9 T10) T11
    INVARIANT_PATTERN = re.compile(
        r"(T0)(.*?)(T1)(?!\d)(.*?)"
        r"((T2)(.*?)(T5)|(T3)(.*?)(T4))(.*?)"
        r"((T7)(.*?)(T8)|(T6)(.*?)(T9)(.*?)(T10))(.*?)"
        r"(T11)(.*?)",
        re.DOTALL,
    )

    # Patrón de reemplazo: preserva contenido en grupos pares (.*?)
    REPLACEMENT_PATTERN = r"\g<2>\g<4>\g<7>\g<10>\g<12>\g<15>\g<18>\g<20>\g<22>\g<24>"

    # Definición de invariantes de transicion para clasificación
    TRANSITION_INVARIANTS = {
        "T0T1T2T5T6T9T10T11": "Variante A (T2-T5→T6-T9-T10 = Served1 → Confirmation)",
        "T0T1T2T5T7T8T11": "Variante B (T2-T5→T7-T8 = Served1 → Cancellation)",
        "T0T1T3T4T6T9T10T11": "Variante C (T3-T4→T6-T9-T10 = Served2 → Confirmation)",
        "T0T1T3T4T7T8T11": "Variante D (T3-T4→T7-T8 = Served2 → Cancellation)",
    }

    def __init__(self, log_file: str | Path, output_file: str | Path = None):
        """
        Inicializa el validador.

        Args:
            log_file: Ruta al archivo de log de transiciones
            output_file: Ruta opcional para guardar el reporte (default: console)
        """
        self.log_file = Path(log_file)
        self.output_file = Path(output_file) if output_file else None
        self._setup_logging()
        self.logger = logging.getLogger(__name__)

    def _setup_logging(self) -> None:
        """Configura el sistema de logging."""
        logger = logging.getLogger(__name__)
        logger.setLevel(logging.DEBUG)

        # Formato estándar
        formatter = logging.Formatter(
            "%(asctime)s - %(levelname)s - %(message)s", datefmt="%Y-%m-%d %H:%M:%S"
        )

        # Handler para consola
        console_handler = logging.StreamHandler()
        console_handler.setLevel(logging.INFO)
        console_handler.setFormatter(formatter)
        logger.addHandler(console_handler)

        # Handler para archivo (si se especifica)
        if self.output_file:
            file_handler = logging.FileHandler(
                self.output_file, mode="w", encoding="utf-8"
            )
            file_handler.setLevel(logging.DEBUG)
            file_handler.setFormatter(formatter)
            logger.addHandler(file_handler)

    def _load_transitions(self) -> str:
        """
        Carga las transiciones desde el archivo de log.

        Returns:
            Cadena conteniendo todas las transiciones

        Raises:
            FileNotFoundError: Si el archivo no existe
            ValueError: Si el archivo está vacío
        """
        if not self.log_file.exists():
            raise FileNotFoundError(f"Archivo no encontrado: {self.log_file}")

        content = self.log_file.read_text(encoding="utf-8").strip()

        if not content:
            raise ValueError("El archivo de transiciones está vacío")

        # Normalizar: remover espacios y saltos de línea (aunque nosotros lo hacemos sin saltos, por si acaso)
        return content.replace("\n", "")

    def _classify_invariant(self, match_str: str) -> str:
        """
        Clasifica el invariante encontrado según su estructura.

        Args:
            match_str: Cadena del invariante encontrado

        Returns:
            Clasificación del invariante
        """
        # Método de clasificación idéntico al original:
        # Verificar condiciones en orden específico
        if (
            "T3" in match_str
            and "T4" in match_str
            and "T7" in match_str
            and "T8" in match_str
        ):
            return "T0T1T3T4T7T8T11"
        elif (
            "T3" in match_str
            and "T4" in match_str
            and "T6" in match_str
            and "T9" in match_str
            and "T10" in match_str
        ):
            return "T0T1T3T4T6T9T10T11"
        elif (
            "T2" in match_str
            and "T5" in match_str
            and "T7" in match_str
            and "T8" in match_str
        ):
            return "T0T1T2T5T7T8T11"
        else:
            return "T0T1T2T5T6T9T10T11"

    def _count_invariants_in_text(
        self, text: str, invariant_counts: Dict[str, int]
    ) -> int:
        """
        Cuenta e identifica los invariantes encontrados en el texto.

        Args:
            text: Cadena con transiciones
            invariant_counts: Diccionario para acumular conteos

        Returns:
            Número de invariantes encontrados en esta pasada
        """
        count = 0
        for match in self.INVARIANT_PATTERN.finditer(text):
            matched_str = match.group(0)
            variant = self._classify_invariant(matched_str)
            invariant_counts[variant] += 1
            count += 1

        return count

    def _count_transitions_in_text(self, text: str) -> Dict[str, int]:
        """
        Cuenta ocurrencias de cada transición T0..T11 en el texto original.

        Args:
            text: Cadena con transiciones

        Returns:
            Diccionario con conteo por transición
        """
        transition_counter = Counter(re.findall(r"T\d+", text))
        return {f"T{i}": transition_counter.get(f"T{i}", 0) for i in range(12)}

    def validate(self) -> ValidationResult:
        """
        Valida los invariantes en el archivo de log.

        Utiliza búsqueda recursiva iterativa:
        1. Busca invariantes y los cuenta
        2. Los reemplaza preservando contenido
        3. Repite hasta que no haya más coincidencias

        Returns:
            ValidationResult con los resultados de la validación
        """
        self.logger.info("=" * 70)
        self.logger.info("VALIDACIÓN DE INVARIANTES - RED DE PETRI")
        self.logger.info("=" * 70)

        try:
            transitions = self._load_transitions()
            self.logger.info("Transiciones cargadas (%s caracteres)", len(transitions))

        except (FileNotFoundError, ValueError) as e:
            self.logger.error("Error al cargar el archivo: %s", e)
            return ValidationResult(
                is_valid=False, total_invariants=0, error_message=str(e)
            )

        # Variables de control
        invariant_counts: Dict[str, int] = {v: 0 for v in self.TRANSITION_INVARIANTS}
        iteration = 0
        original_transitions = transitions
        original_length = len(transitions)
        total_invariants = 0
        transition_counts = self._count_transitions_in_text(original_transitions)

        # PRIMERA PASADA: contar invariantes y reemplazar
        current_text = transitions
        count = self._count_invariants_in_text(current_text, invariant_counts)
        total_invariants += count

        if count > 0:
            iteration += 1
            self.logger.debug(f"Iteración {iteration}: Encontrados {count} invariantes")
            new_text, replacements = self.INVARIANT_PATTERN.subn(
                self.REPLACEMENT_PATTERN, current_text
            )
            current_text = new_text

            # ITERACIONES SIGUIENTES: mientras haya reemplazos
            while replacements > 0:
                iteration += 1
                count = self._count_invariants_in_text(current_text, invariant_counts)
                total_invariants += count

                if count > 0:
                    self.logger.debug(
                        f"Iteración {iteration}: Encontrados {count} invariantes"
                    )

                new_text, replacements = self.INVARIANT_PATTERN.subn(
                    self.REPLACEMENT_PATTERN, current_text
                )
                current_text = new_text

        transitions = current_text

        # Determinar resultado final
        is_valid = len(transitions) == 0

        # Logging de resultados
        self._log_results(
            is_valid,
            total_invariants,
            invariant_counts,
            transition_counts,
            transitions,
            original_length,
            iteration,
        )

        return ValidationResult(
            is_valid=is_valid,
            total_invariants=total_invariants,
            invariant_counts=invariant_counts,
            remaining_transitions=transitions,
            error_message=(
                "" if is_valid else f"{len(transitions)} caracteres sin procesar"
            ),
        )

    def _log_results(
        self,
        is_valid: bool,
        total: int,
        counts: Dict[str, int],
        transition_counts: Dict[str, int],
        remaining: str,
        original_len: int,
        iterations: int,
    ) -> None:
        """Registra los resultados en formato detallado."""
        self.logger.info("-" * 70)
        self.logger.info("RESULTADOS (Iteraciones: %s)", iterations)
        self.logger.info("-" * 70)

        # Estado final
        status = "✓ VÁLIDO" if is_valid else "✗ INVÁLIDO"
        self.logger.info("Estado: %s", status)
        self.logger.info("Invariantes encontrados: %s", total)
        self.logger.info(
            "Transiciones procesadas: %s / %s",
            original_len - len(remaining),
            original_len,
        )

        # Desglose por variante
        self.logger.info("Invariantes por variante:")
        for variant_key, description in self.TRANSITION_INVARIANTS.items():
            count = counts[variant_key]
            self.logger.info("  • %s: %s - %s", variant_key, count, description)

        self.logger.info("Conteo de transiciones en archivo:")
        for transition, count in transition_counts.items():
            self.logger.info("  • %s: %s", transition, count)

        # Transiciones sin procesar (si las hay)
        if remaining:
            self.logger.warning("Transiciones sin procesar (%s car.):", len(remaining))
            self.logger.warning("  %s", remaining)
        else:
            self.logger.info("✓ Todas las transiciones fueron procesadas correctamente")

        self.logger.info("=" * 70)


def main():
    """Función principal para ejecutar el validador."""
    import sys

    # Configuración
    log_file = Path(__file__).parent / "logs" / "transitions.log"
    output_file = Path(__file__).parent / "logs" / "validation_report.log"

    # Crear directorio de logs si no existe
    log_file.parent.mkdir(parents=True, exist_ok=True)

    # Ejecutar validación
    validator = PetriNetValidator(log_file, output_file)

    try:
        result = validator.validate()

        # Retornar código de salida apropiado
        sys.exit(0 if result.is_valid else 1)

    except Exception as e:
        print(f"Error fatal: {e}", file=sys.stderr)
        sys.exit(2)


if __name__ == "__main__":
    main()
