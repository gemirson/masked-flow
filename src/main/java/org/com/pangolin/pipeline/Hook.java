package org.com.pangolin.pipeline;

import java.util.function.Function;

// ============================================================================
// Hook: Agora genérico para um contexto <T> fortemente tipado.

// ============================================================================

/**
 * Representa uma unidade de lógica de negócios (um "gancho") dentro de um {@link Pipeline}.
 * <p>
 * Cada hook é associado a um ou mais caminhos (definidos por uma máscara de bits),
 * tem uma prioridade de execução e encapsula uma função que opera sobre um contexto
 * de dados genérico {@code <T>}.
 * </p>
 * <p>
 * Hooks são {@link Comparable}, permitindo que sejam ordenados com base em sua prioridade.
 * A classe é imutável após a sua criação.
 * </p>
 *
 * @param <T> O tipo do objeto de contexto que este hook processa.
 */
class Hook<T> implements Comparable<Hook<T>> {
    private final String name;
    private final int paths;
    private final int priority; // << NOVO
    private final Function<T, T> function;

    /**
     * Construtor para criar um novo Hook.
     *
     * @param name     O nome do hook, usado para identificação e depuração.
     * @param paths    A máscara de bits que define em quais caminhos ({@link Path}) este hook deve ser executado.
     * @param priority A prioridade de execução. Um valor menor indica uma prioridade maior.
     * @param function A função que contém a lógica a ser executada. Ela pode ser expressa como $f: T \rightarrow T$.
     */
    public Hook(String name, int paths, int priority, Function<T, T> function) {
        this.name = name;
        this.paths = paths;
        this.priority = priority;
        this.function = function;
    }

    /**
     * Decide se este hook deve ser executado para um determinado {@link Path}.
     * <p>
     * A decisão é baseada na correspondência de bits entre o caminho fornecido e os caminhos
     * associados a este hook. A verificação é uma operação de tempo constante $$O(1)$$.
     * </p>
     *
     * @param path O caminho de execução atual do pipeline.
     * @return {@code true} se o hook deve ser executado, {@code false} caso contrário.
     */
    public boolean shouldExecute(Path path) {
        return path.matches(paths);
    }

    /**
     * Executa a lógica do hook aplicando a função encapsulada ao contexto fornecido.
     *
     * @param context O objeto de contexto atual do pipeline, $c \in T$.
     * @return O novo estado do contexto $c' = f(c)$, onde $c' \in T$.
     */
    public T execute(T context) {
        return function.apply(context);
    }

    /**
     * Retorna o nome do hook.
     *
     * @return O nome do hook.
     */
    public String getName() { return name; }

    /**
     * Retorna a prioridade do hook.
     *
     * @return O valor da prioridade.
     */
    public int getPriority() { return priority; }

    /**
     * Compara este hook com outro com base em suas prioridades.
     * <p>
     * A ordenação é definida pela prioridade $p$. Para dois hooks $h_1$ e $h_2$ com
     * prioridades $p_1$ e $p_2$, a relação de ordem é $h_1 < h_2$ se $p_1 < p_2$.
     * </p>
     *
     * @param other O outro hook a ser comparado.
     * @return Um valor negativo se $p_1 < p_2$, zero se $p_1 = p_2$, ou um valor positivo se $p_1 > p_2$.
     */
    @Override
    public int compareTo(Hook<T> other) {
        // Ordenação por prioridade. Menor número vem primeiro.
        return Integer.compare(this.priority, other.priority);
    }
}
