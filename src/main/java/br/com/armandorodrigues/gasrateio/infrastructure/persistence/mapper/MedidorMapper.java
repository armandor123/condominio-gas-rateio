package br.com.armandorodrigues.gasrateio.infrastructure.persistence.mapper;

import br.com.armandorodrigues.gasrateio.domain.model.Medidor;
import br.com.armandorodrigues.gasrateio.infrastructure.persistence.entity.MedidorEntity;

public class MedidorMapper {

    private MedidorMapper() {
    }

    public static MedidorEntity toEntity(Medidor medidor) {
        return new MedidorEntity(
                medidor.getId(),
                medidor.getNome(),
                medidor.getCodigo(),
                medidor.getTipo(),
                medidor.getTorreId(),
                medidor.isAtivo()
        );
    }

    public static Medidor toDomain(MedidorEntity entity) {
        return new Medidor(
                entity.getId(),
                entity.getNome(),
                entity.getCodigo(),
                entity.getTipo(),
                entity.getTorreId(),
                entity.isAtivo()
        );
    }
}
