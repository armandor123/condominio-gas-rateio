package br.com.armandorodrigues.gasrateio.infrastructure.persistence.mapper;

import br.com.armandorodrigues.gasrateio.domain.model.ContaMensal;
import br.com.armandorodrigues.gasrateio.infrastructure.persistence.entity.ContaMensalEntity;

import java.time.YearMonth;

public class ContaMensalMapper {

    private ContaMensalMapper() {
    }

    public static ContaMensalEntity toEntity(ContaMensal contaMensal) {
        return new ContaMensalEntity(
                contaMensal.getId(),
                contaMensal.getMesReferencia().toString(),
                contaMensal.getValorTotal(),
                contaMensal.getConsumoInformado(),
                contaMensal.getDataVencimento(),
                contaMensal.getNumeroFatura(),
                contaMensal.getObservacoes()
        );
    }

    public static ContaMensal toDomain(ContaMensalEntity entity) {
        return new ContaMensal(
                entity.getId(),
                YearMonth.parse(entity.getMesReferencia()),
                entity.getValorTotal(),
                entity.getConsumoInformado(),
                entity.getDataVencimento(),
                entity.getNumeroFatura(),
                entity.getObservacoes()
        );
    }
}