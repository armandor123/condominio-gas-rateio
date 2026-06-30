package br.com.armandorodrigues.gasrateio.application.usecase;

import br.com.armandorodrigues.gasrateio.application.dto.ContaMensalResponse;
import br.com.armandorodrigues.gasrateio.domain.repository.ContaMensalRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListarContasMensaisUseCase {

    private final ContaMensalRepository contaMensalRepository;

    public ListarContasMensaisUseCase(ContaMensalRepository contaMensalRepository) {
        this.contaMensalRepository = contaMensalRepository;
    }

    public List<ContaMensalResponse> executar() {
        return contaMensalRepository.listarTodas()
                .stream()
                .map(conta -> new ContaMensalResponse(
                        conta.getId(),
                        conta.getMesReferencia(),
                        conta.getValorTotal(),
                        conta.getConsumoInformado(),
                        conta.getDataVencimento(),
                        conta.getNumeroFatura(),
                        conta.getObservacoes()
                ))
                .toList();
    }
}