## Grafico

Segun el grafico y los calculos en base al paper, para la RdP hacen falta 8 hilos, cada uno se encarga de las siguientes transiciones:

1. Door: T0 y T1
2. Agent 1: T2
3. Agent 2: T3
4. Served 1: T5
5. Served 2: T4
6. Confirmation: T6, T9 y T10
7. Cancellation: T7 y T8
8. Exit: T11

## Transiciones que cubren ustedes

- T0 y T1 -> Door
- (T2 + T5) y (T3 + T4) -> Agent 1 y 2 con Served 1 y 2
- T2 y T3 -> Red
- T6, T9 y T10 -> Confirmation
- T7 y T8 -> Cancellation
- T11 -> Exit

### **Problema**

En main terminan haciendo 8 hilos, asi que esta bien, pero el problema que esta habiendo ahora es que Red y Agent comparten transiciones, y tambien que Agent se esta encargando de 4 transiciones y no solamente de 2
