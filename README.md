# Princípios do Fluxo Mascarado

## I. Definições

Seja um universo de computação definido pelos seguintes elementos primordiais:

**Definição 1: Caminho Elementar (Path)**
Seja $P$ o conjunto finito de caminhos elementares e ortogonais, onde cada caminho $p_i \in P$ é representado por um inteiro único da forma $p_i = 2^{i-1}$ para $i \in \{1, 2, \dots, N\}$.
$$P = \{p_1, p_2, \dots, p_N\} = \{1, 2, 4, \dots, 2^{N-1}\}$$

**Definição 2: Máscara de Ativação (Activation Mask)**
Uma máscara de ativação $M$ é um inteiro não-negativo formado pela união bit-a-bit (operador $|$) de um subconjunto de caminhos elementares de $P$. A máscara $M$ representa um conjunto de condições de ativação.

**Definição 3: Contexto (Context)**
Seja $C$ o espaço de estados, representando o universo de todos os contextos possíveis. Um contexto $c \in C$ é a entidade que carrega os dados através do fluxo.

**Definição 4: Gancho (Hook)**
Um gancho (hook) $h$ é um par ordenado $(M, \phi)$ onde:
- $M$ é uma máscara de ativação.
- $\phi: C \to C$ é uma função de transformação que mapeia um contexto a outro.

**Definição 5: Fluxo (Flow)**
Um fluxo $\Pi$ é uma sequência ordenada (uma n-tupla) de $n$ ganchos:
$$\Pi = \langle h_1, h_2, \dots, h_n \rangle$$
Onde $h_k = (M_k, \phi_k)$ para $k \in \{1, \dots, n\}$. A ordem da sequência é fundamental.

## II. Proposição I: Execução do Fluxo

A execução de um fluxo $\Pi$ para um dado caminho elementar $p \in P$ e um contexto inicial $c_0 \in C$ é uma função $E: (\Pi, p, c_0) \mapsto c_n$, que produz um contexto final $c_n$.

A execução é definida pela seguinte relação de recorrência sobre a sequência de contextos $\langle c_0, c_1, \dots, c_n \rangle$:

Para cada $k$ de $1$ a $n$, o contexto $c_k$ é determinado por:
$$
c_k =
\begin{cases}
  \phi_k(c_{k-1}) & \text{se } (p \& M_k) \neq 0 \\
  c_{k-1} & \text{se } (p \& M_k) = 0
\end{cases}
$$

Onde `&` denota a operação AND bit-a-bit. A condição $(p \& M_k) \neq 0$ é o predicado de ativação, que avalia se o caminho $p$ está contido no conjunto de ativação definido pela máscara $M_k$.

O resultado final da execução, $E(\Pi, p, c_0)$, é o contexto $c_n$.

## III. Corolário

- **Determinismo**: Para um fluxo $\Pi$, um caminho $p$ e um contexto inicial $c_0$ fixos, o contexto final $c_n$ é único e inequivocamente determinado. O sistema é, por construção, determinístico.

- **Complexidade**: A avaliação do predicado de ativação $(p \& M_k)$ é uma operação de tempo constante, $O(1)$, com respeito ao número total de caminhos $N$. A complexidade computacional total da função de execução $E$ é, portanto, linear com respeito ao número de ganchos no fluxo, $O(n)$.
