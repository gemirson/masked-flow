package org.com.pangolin.pipeline;


// ============================================================================
// Path: Definição de caminhos via bitmasks. Intocável. É a parte boa.
// ============================================================================
/**
 * Representa um caminho ou um conjunto de caminhos de execução em um pipeline,
 * utilizando máscaras de bits (bitmasks) para uma verificação de correspondência eficiente.
 * <p>
 * A classe é imutável. Uma vez que um objeto {@code Path} é criado, seu valor (máscara)
 * não pode ser alterado.
 * </p>
 * <p>
 * A utilização de bitmasks permite que a verificação de correspondência seja uma operação
 * de tempo constante $O(1)$, o que é crucial para o desempenho do pipeline.
 * </p>
 */
public class Path {
    /**
     * Máscara de bits para o Caminho A. Valor: $2^0 = 1$ (0b001).
     */
    public static final int PATH_A = 1 << 0; // 0b001
    /**
     * Máscara de bits para o Caminho B. Valor: $2^1 = 2$ (0b010).
     */
    public static final int PATH_B = 1 << 1; // 0b010
    /**
     * Máscara de bits para o Caminho C. Valor: $2^2 = 4$ (0b100).
     */
    public static final int PATH_C = 1 << 2; // 0b100
    /**
     * Máscara de bits que representa todos os caminhos combinados (A, B e C).
     * Corresponde à operação de união $A \cup B \cup C$.
     */
    public static final int ALL = PATH_A | PATH_B | PATH_C;

    private final int mask;

    /**
     * Construtor para criar uma nova instância de Path com uma máscara específica.
     * A instanciação é preferencialmente feita através do método de fábrica {@link #of(int)}.
     *
     * @param mask A máscara de bits que define este caminho.
     */
    public Path(int mask) { this.mask = mask; }

    /**
     * Retorna a máscara de bits associada a esta instância de Path.
     *
     * @return O valor inteiro da máscara de bits.
     */
    public int getMask() { return mask; }

    /**
     * Verifica se este caminho corresponde a uma determinada máscara de caminhos de um hook.
     * <p>
     * A correspondência é definida pela operação de interseção de bits. A condição para
     * correspondência é que a interseção não seja vazia. Formalmente, a verificação é:
     * $(mask \ \&\ hookPaths) \neq 0$.
     * A operação é realizada em tempo constante $O(1)$.
     * </p>
     *
     * @param hookPaths A máscara de bits dos caminhos definidos para um hook.
     * @return {@code true} se houver uma correspondência, {@code false} caso contrário.
     */
    public boolean matches(int hookPaths) {
        return (mask & hookPaths) != 0;
    }

    /**
     * Método de fábrica para criar uma nova instância de {@code Path}.
     * Esta é a forma preferencial de criar objetos Path, pois melhora a legibilidade.
     *
     * @param mask A máscara de bits para o novo caminho.
     * @return Uma nova instância de {@link Path}.
     */
    public static Path of(int mask) { return new Path(mask); }
}
