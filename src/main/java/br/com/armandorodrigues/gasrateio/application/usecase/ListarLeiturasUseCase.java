package br.com.armandorodrigues.gasrateio.application.usecase;

import br.com.armandorodrigues.gasrateio.application.dto.LeituraResponse;
import br.com.armandorodrigues.gasrateio.domain.repository.LeituraRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListarLeiturasUseCase {

    private final LeituraRepository leituraRepository;

    public ListarLeiturasUseCase(LeituraRepository leituraRepository) {
        this.leituraRepository = leituraRepository;
    }

    public List<LeituraResponse> executar() {
        return leituraRepository.listarTodas()
                .stream()
                .map(leitura -> new LeituraResponse(
                        leitura.getId(),
                        leitura.getMedidorId(),
                        leitura.getMesReferencia(),
                        leitura.getDataLeitura(),
                        leitura.getLeituraAnterior(),
                        leitura.getLeituraAtual(),
                        leitura.getConsumo()
                ))
                .toList();
    }
}