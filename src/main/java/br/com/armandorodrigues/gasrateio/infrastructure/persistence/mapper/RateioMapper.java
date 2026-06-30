package br.com.armandorodrigues.gasrateio.infrastructure.persistence.mapper;

import br.com.armandorodrigues.gasrateio.domain.model.ItemRateio;
import br.com.armandorodrigues.gasrateio.domain.model.Rateio;
import br.com.armandorodrigues.gasrateio.infrastructure.persistence.entity.ItemRateioEntity;
import br.com.armandorodrigues.gasrateio.infrastructure.persistence.entity.RateioEntity;

import java.time.YearMonth;
import java.util.List;

public class RateioMapper {

    private RateioMapper() {
    }

    public static RateioEntity toEntity(Rateio rateio) {
        RateioEntity entity = new RateioEntity(
                rateio.getId(),
                rateio.getMesReferencia().toString(),
                rateio.getContaMensalId(),
                rateio.getValorTotalConta(),
                rateio.getConsumoTotalSecundario(),
                rateio.getConsumoMedidorPrincipal(),
                rateio.getDiferencaConsumo(),
                rateio.getDataCalculo()
        );

        rateio.getItens()
                .stream()
                .map(RateioMapper::toItemEntity)
                .forEach(entity::adicionarItem);

        return entity;
    }

    public static Rateio toDomain(RateioEntity entity) {
        List<ItemRateio> itens = entity.getItens()
                .stream()
                .map(RateioMapper::toItemDomain)
                .toList();

        return new Rateio(
                entity.getId(),
                YearMonth.parse(entity.getMesReferencia()),
                entity.getContaMensalId(),
                entity.getValorTotalConta(),
                entity.getConsumoTotalSecundario(),
                entity.getConsumoMedidorPrincipal(),
                entity.getDiferencaConsumo(),
                entity.getDataCalculo(),
                itens
        );
    }

    private static ItemRateioEntity toItemEntity(ItemRateio item) {
        return new ItemRateioEntity(
                item.getId(),
                item.getTorreId(),
                item.getNomeTorre(),
                item.getMedidorId(),
                item.getConsumo(),
                item.getPercentual(),
                item.getValorRateado()
        );
    }

    private static ItemRateio toItemDomain(ItemRateioEntity entity) {
        return new ItemRateio(
                entity.getId(),
                entity.getTorreId(),
                entity.getNomeTorre(),
                entity.getMedidorId(),
                entity.getConsumo(),
                entity.getPercentual(),
                entity.getValorRateado()
        );
    }
}