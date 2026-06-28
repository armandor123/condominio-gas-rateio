package br.com.armandorodrigues.gasrateio.infrastructure.persistence.mapper;

import br.com.armandorodrigues.gasrateio.domain.model.Torre;
import br.com.armandorodrigues.gasrateio.infrastructure.persistence.entity.TorreEntity;

public class TorreMapper {

    private TorreMapper() {
    }

    public static TorreEntity toEntity(Torre torre) {
        return new TorreEntity(
                torre.getId(),
                torre.getNome(),
                torre.isAtiva()
        );
    }

    public static Torre toDomain(TorreEntity entity) {
        return new Torre(
                entity.getId(),
                entity.getNome(),
                entity.isAtiva()
        );
    }
}
