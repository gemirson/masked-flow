package org.com.pangolin.pipeline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

// ===========================================================================
// Pipeline: Imutável e fortemente tipado, construído via Builder.
// ===========================================================================
public class Pipeline<T> {
    private final List<Hook<T>> hooks;

    // Construtor privado. A única forma de criar um Pipeline é via Builder.
    private Pipeline(List<Hook<T>> hooks) {
        // Garante que a lista de hooks seja imutável após a construção.
        this.hooks = Collections.unmodifiableList(new ArrayList<>(hooks));
    }

    /**
     * Executa o pipeline com um contexto inicial fortemente tipado.
     * <p>
     * A complexidade temporal é $O(n)$, onde $n$ é o número total de hooks no pipeline.
     * A decisão de execução para cada hook individual é $O(1)$.
     * </p>
     * <p>
     * Para um contexto inicial $c_0$ e uma lista ordenada de hooks aplicáveis $[h_1, h_2, ..., h_k]$,
     * o contexto final $c_k$ é o resultado da composição de funções:
     * $c_k = (h_k \circ h_{k-1} \circ ... \circ h_1)(c_0)$.
     * </p>
     * @param path O caminho a ser avaliado para decidir se um hook deve ser executado.
     * @param initialContext O contexto inicial, $c_0$.
     * @return O contexto final, $c_k$, após a execução de todos os hooks aplicáveis.
     */
    public T execute(Path path, T initialContext) {
        T currentContext = initialContext;
        for (Hook<T> hook : hooks) {
            if (hook.shouldExecute(path)) {
                currentContext = hook.execute(currentContext);
            }
        }
        return currentContext;
    }

    /**
     * Retorna o número de hooks registrados neste pipeline, $n$.
     *
     * @return o número de hooks.
     */
    public int getHookCount() {
        return hooks.size();
    }

    // ========================================================================
    // Builder: A maneira correta de construir um objeto complexo e imutável.
    // ========================================================================
    public static class Builder<T> {
        private final List<Hook<T>> hooks = new ArrayList<>();

        /**
         * Registra um novo hook no pipeline com um nome, caminhos (paths) e prioridade.
         *
         * @param name o nome do hook, para identificação.
         * @param paths uma máscara de bits representando os caminhos em que este hook deve ser executado.
         * @param priority a prioridade de execução do hook. Menor valor indica maior prioridade.
         * @param fn a função que representa a lógica do hook, formalmente $f: T \rightarrow T$.
         * @return o próprio Builder, para encadeamento de chamadas (fluent interface).
         */
        public Builder<T> register(String name, int paths, int priority, Function<T, T> fn) {
            this.hooks.add(new Hook<>(name, paths, priority, fn));
            return this;
        }

        /**
         * Constrói o pipeline.
         * <p>
         * A construção tem uma complexidade de $O(n \log n)$ devido à ordenação dos hooks,
         * onde $n$ é o número de hooks registrados. Após a ordenação, a lista de hooks
         * torna-se imutável para garantir a consistência do pipeline.
         * </p>
         *
         * @return uma nova instância imutável de {@link Pipeline}.
         */
        public Pipeline<T> build() {
            // 1. Ordena a lista de hooks UMA VEZ. A PriorityQueue é implícita aqui.
            Collections.sort(hooks);

            // 2. Cria uma cópia imutável para garantir a segurança.
            List<Hook<T>> immutableSortedHooks = Collections.unmodifiableList(hooks);

            // 3. Retorna o Pipeline pronto para execução.
            return new Pipeline<>(immutableSortedHooks);
        }
    }
}
