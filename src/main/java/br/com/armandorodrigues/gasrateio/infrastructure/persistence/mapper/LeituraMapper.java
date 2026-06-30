package br.com.armandorodrigues.gasrateio.infrastructure.persistence.mapper;

import br.com.armandorodrigues.gasrateio.domain.model.Leitura;
import br.com.armandorodrigues.gasrateio.infrastructure.persistence.entity.LeituraEntity;

import java.time.YearMonth;

public class LeituraMapper {

    private LeituraMapper() {
    }

    public static LeituraEntity toEntity(Leitura leitura) {
        return new LeituraEntity(
                leitura.getId(),
                leitura.getMedidorId(),
                leitura.getMesReferencia().toString(),
                leitura.getDataLeitura(),
                leitura.getLeituraAnterior(),
                leitura.getLeituraAtual(),
                leitura.getConsumo()
        );
    }

    public static Leitura toDomain(LeituraEntity entity) {
        return new Leitura(
                entity.getId(),
                entity.getMedidorId(),
                YearMonth.parse(entity.getMesReferencia()),
                entity.getDataLeitura(),
                entity.getLeituraAnterior(),
                entity.getLeituraAtual()
        );
    }
}