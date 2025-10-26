package org.com.pangolin;

// ============================================================================
// Contextos: POJOs fortemente tipados. Onde os dados vivem.
// ============================================================================

import org.com.pangolin.pipeline.Pipeline;
import org.com.pangolin.pipeline.Path;
/**
 * Contexto para o exemplo de processamento de pedidos.
 * Sem ambiguidades. O compilador garante que os tipos estão corretos.
 */
class OrderContext {
    // Dados de entrada
    String orderId;
    double price;

    // Dados de saída e intermediários
    String type;
    double shipping = 0.0;
    double total = 0.0;
    boolean valid = false;
    boolean inStock = false;
    String priority;
    boolean customsRequired = false;

    public OrderContext(String orderId, double price) {
        this.orderId = orderId;
        this.price = price;
    }

    @Override
    public String toString() {
        return String.format(
                "OrderContext[id=%s, type=%s, price=%.2f, shipping=%.2f, total=%.2f, valid=%s, inStock=%s]",
                orderId, type, price, shipping, total, valid, inStock
        );
    }
}

// ============================================================================
// Classe Principal: Demonstração do padrão refatorado.
// ============================================================================
public class KernelFlowPatternRefactored {

    public static void main(String[] args) {
        System.out.println("=== EXEMPLO: Processamento de Pedidos (Fortemente Tipado) ===\n");

        // 1. Construir o pipeline UMA VEZ usando o Builder.
        Pipeline.Builder<OrderContext> builder = new Pipeline.Builder<>();

        // Validação comum (todos os paths)
        builder.register("validate_order", Path.ALL, ctx -> {
            ctx.valid = ctx.orderId != null && !ctx.orderId.isEmpty();
            return ctx;
        });

        // Path A: Pedido Normal
        builder.register("calc_shipping_normal", Path.PATH_A, ctx -> {
            ctx.shipping = 10.0;
            ctx.type = "Normal";
            return ctx;
        });

        // Path B e C: Verificação de estoque (comum)
        builder.register("check_stock", Path.PATH_B | Path.PATH_C, ctx -> {
            ctx.inStock = true; // Simulação
            return ctx;
        });

        // Path B: Pedido Express
        builder.register("calc_shipping_express", Path.PATH_B, ctx -> {
            ctx.shipping = 25.0;
            ctx.priority = "high";
            ctx.type = "Express";
            return ctx;
        });

        // Path C: Pedido Internacional
        builder.register("calc_shipping_intl", Path.PATH_C, ctx -> {
            ctx.shipping = 50.0;
            ctx.customsRequired = true;
            ctx.type = "International";
            return ctx;
        });

        // Finalização comum: cálculo do total
        builder.register("calculate_total", Path.ALL, ctx -> {
            ctx.total = ctx.price + ctx.shipping;
            return ctx;
        });

        // O pipeline agora é um objeto imutável.
        Pipeline<OrderContext> orderPipeline = builder.build();

        // 2. Executar o pipeline para diferentes caminhos.

        System.out.println(">>> PEDIDO NORMAL (Path A):");
        OrderContext normalOrder = new OrderContext("ORD001", 100.0);
        OrderContext resultA = orderPipeline.execute(Path.of(Path.PATH_A), normalOrder);
        System.out.println("  Resultado: " + resultA);
        System.out.println();

        System.out.println(">>> PEDIDO EXPRESS (Path B):");
        OrderContext expressOrder = new OrderContext("ORD002", 100.0);
        OrderContext resultB = orderPipeline.execute(Path.of(Path.PATH_B), expressOrder);
        System.out.println("  Resultado: " + resultB);
        System.out.printf("  Detalhes: Priority=%s, In Stock=%s%n\n", resultB.priority, resultB.inStock);

        System.out.println(">>> PEDIDO INTERNACIONAL (Path C):");
        OrderContext intlOrder = new OrderContext("ORD003", 100.0);
        OrderContext resultC = orderPipeline.execute(Path.of(Path.PATH_C), intlOrder);
        System.out.println("  Resultado: " + resultC);
        System.out.printf("  Detalhes: Customs Required=%s, In Stock=%s%n\n", resultC.customsRequired, resultC.inStock);
    }
}