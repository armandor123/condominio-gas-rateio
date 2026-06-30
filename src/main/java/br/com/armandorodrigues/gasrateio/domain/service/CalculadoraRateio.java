package br.com.armandorodrigues.gasrateio.domain.service;

import br.com.armandorodrigues.gasrateio.domain.exception.RegraNegocioException;
import br.com.armandorodrigues.gasrateio.domain.model.ContaMensal;
import br.com.armandorodrigues.gasrateio.domain.model.ItemRateio;
import br.com.armandorodrigues.gasrateio.domain.model.Rateio;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CalculadoraRateio {

    private static final int ESCALA_DINHEIRO = 2;
    private static final int ESCALA_PERCENTUAL = 2;
    private static final int ESCALA_CALCULO = 10;

    public Rateio calcular(
            ContaMensal contaMensal,
            BigDecimal consumoMedidorPrincipal,
            List<DadosConsumoTorre> consumosTorres
    ) {
        validarContaMensal(contaMensal);
        validarConsumoMedidorPrincipal(consumoMedidorPrincipal);
        validarConsumosTorres(consumosTorres);

        BigDecimal consumoTotalSecundario = calcularConsumoTotalSecundario(consumosTorres);

        if (consumoTotalSecundario.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraNegocioException("O consumo total dos medidores secundários deve ser maior que zero.");
        }

        BigDecimal diferencaConsumo = consumoMedidorPrincipal.subtract(consumoTotalSecundario);

        List<ItemRateio> itens = calcularItens(
                contaMensal.getValorTotal(),
                consumoTotalSecundario,
                consumosTorres
        );

        return Rateio.novo(
                contaMensal.getMesReferencia(),
                contaMensal.getId(),
                contaMensal.getValorTotal().setScale(ESCALA_DINHEIRO, RoundingMode.HALF_UP),
                consumoTotalSecundario,
                consumoMedidorPrincipal,
                diferencaConsumo,
                itens
        );
    }

    private void validarContaMensal(ContaMensal contaMensal) {
        if (contaMensal == null) {
            throw new RegraNegocioException("A conta mensal é obrigatória para calcular o rateio.");
        }

        if (contaMensal.getId() == null || contaMensal.getId() <= 0) {
            throw new RegraNegocioException("A conta mensal precisa estar salva antes do cálculo do rateio.");
        }
    }

    private void validarConsumoMedidorPrincipal(BigDecimal consumoMedidorPrincipal) {
        if (consumoMedidorPrincipal == null) {
            throw new RegraNegocioException("O consumo do medidor principal é obrigatório.");
        }

        if (consumoMedidorPrincipal.compareTo(BigDecimal.ZERO) < 0) {
            throw new RegraNegocioException("O consumo do medidor principal não pode ser negativo.");
        }
    }

    private void validarConsumosTorres(List<DadosConsumoTorre> consumosTorres) {
        if (consumosTorres == null || consumosTorres.isEmpty()) {
            throw new RegraNegocioException("Não existem leituras suficientes dos medidores secundários para calcular o rateio.");
        }
    }

    private BigDecimal calcularConsumoTotalSecundario(List<DadosConsumoTorre> consumosTorres) {
        return consumosTorres.stream()
                .map(DadosConsumoTorre::consumo)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<ItemRateio> calcularItens(
            BigDecimal valorTotalConta,
            BigDecimal consumoTotalSecundario,
            List<DadosConsumoTorre> consumosTorres
    ) {
        List<ItemRateioCalculado> itensCalculados = new ArrayList<>();

        for (DadosConsumoTorre dados : consumosTorres) {
            BigDecimal percentual = calcularPercentual(dados.consumo(), consumoTotalSecundario);
            BigDecimal valorRateado = calcularValorRateado(valorTotalConta, dados.consumo(), consumoTotalSecundario);

            itensCalculados.add(new ItemRateioCalculado(dados, percentual, valorRateado));
        }

        BigDecimal totalRateado = itensCalculados.stream()
                .map(ItemRateioCalculado::valorRateado)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal diferencaArredondamento = valorTotalConta
                .setScale(ESCALA_DINHEIRO, RoundingMode.HALF_UP)
                .subtract(totalRateado);

        int indiceMaiorConsumo = encontrarIndiceMaiorConsumo(itensCalculados);

        List<ItemRateio> itens = new ArrayList<>();

        for (int i = 0; i < itensCalculados.size(); i++) {
            ItemRateioCalculado itemCalculado = itensCalculados.get(i);

            BigDecimal valorFinal = itemCalculado.valorRateado();

            if (i == indiceMaiorConsumo) {
                valorFinal = valorFinal.add(diferencaArredondamento);
            }

            itens.add(ItemRateio.novo(
                    itemCalculado.dados().torreId(),
                    itemCalculado.dados().nomeTorre(),
                    itemCalculado.dados().medidorId(),
                    itemCalculado.dados().consumo(),
                    itemCalculado.percentual(),
                    valorFinal
            ));
        }

        return itens;
    }

    private BigDecimal calcularPercentual(BigDecimal consumoTorre, BigDecimal consumoTotalSecundario) {
        return consumoTorre
                .divide(consumoTotalSecundario, ESCALA_CALCULO, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(ESCALA_PERCENTUAL, RoundingMode.HALF_UP);
    }

    private BigDecimal calcularValorRateado(
            BigDecimal valorTotalConta,
            BigDecimal consumoTorre,
            BigDecimal consumoTotalSecundario
    ) {
        return valorTotalConta
                .multiply(consumoTorre)
                .divide(consumoTotalSecundario, ESCALA_DINHEIRO, RoundingMode.HALF_UP);
    }

    private int encontrarIndiceMaiorConsumo(List<ItemRateioCalculado> itensCalculados) {
        ItemRateioCalculado maiorItem = itensCalculados.stream()
                .max(Comparator.comparing(item -> item.dados().consumo()))
                .orElseThrow(() -> new RegraNegocioException("Não foi possível identificar o maior consumo do rateio."));

        return itensCalculados.indexOf(maiorItem);
    }

    public record DadosConsumoTorre(
            Long torreId,
            String nomeTorre,
            Long medidorId,
            BigDecimal consumo
    ) {
        public DadosConsumoTorre {
            if (torreId == null || torreId <= 0) {
                throw new RegraNegocioException("A torre é obrigatória para calcular o rateio.");
            }

            if (nomeTorre == null || nomeTorre.trim().isEmpty()) {
                throw new RegraNegocioException("O nome da torre é obrigatório para calcular o rateio.");
            }

            if (medidorId == null || medidorId <= 0) {
                throw new RegraNegocioException("O medidor é obrigatório para calcular o rateio.");
            }

            if (consumo == null) {
                throw new RegraNegocioException("O consumo da torre é obrigatório para calcular o rateio.");
            }

            if (consumo.compareTo(BigDecimal.ZERO) < 0) {
                throw new RegraNegocioException("O consumo da torre não pode ser negativo.");
            }

            nomeTorre = nomeTorre.trim();
        }
    }

    private record ItemRateioCalculado(
            DadosConsumoTorre dados,
            BigDecimal percentual,
            BigDecimal valorRateado
    ) {
    }
}