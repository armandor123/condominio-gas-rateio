package br.com.armandorodrigues.gasrateio.application.usecase;

import br.com.armandorodrigues.gasrateio.application.dto.MedidorResponse;
import br.com.armandorodrigues.gasrateio.domain.repository.MedidorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListarMedidoresUseCase {

    private final MedidorRepository medidorRepository;

    public ListarMedidoresUseCase(MedidorRepository medidorRepository) {
        this.medidorRepository = medidorRepository;
    }

    public List<MedidorResponse> executar() {
        return medidorRepository.listarTodos()
                .stream()
                .map(medidor -> new MedidorResponse(
                        medidor.getId(),
                        medidor.getNome(),
                        medidor.getCodigo(),
                        medidor.getTipo(),
                        medidor.getTorreId(),
                        medidor.isAtivo()
                ))
                .toList();
    }
}