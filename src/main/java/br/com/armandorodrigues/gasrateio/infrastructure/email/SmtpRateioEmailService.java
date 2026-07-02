package br.com.armandorodrigues.gasrateio.infrastructure.email;

import br.com.armandorodrigues.gasrateio.application.service.RateioEmailService;
import br.com.armandorodrigues.gasrateio.domain.model.ItemRateio;
import br.com.armandorodrigues.gasrateio.domain.model.Rateio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;

@Service
@ConditionalOnProperty(name = "app.email.enabled", havingValue = "true")
public class SmtpRateioEmailService implements RateioEmailService {

    private static final Logger log = LoggerFactory.getLogger(SmtpRateioEmailService.class);

    private final JavaMailSender javaMailSender;

    @Value("${app.email.from:no-reply@gasrateio.local}")
    private String remetente;

    @Value("${app.email.destinatarios:}")
    private String destinatarios;

    public SmtpRateioEmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void enviarRateioCalculado(Rateio rateio) {
        if (destinatarios == null || destinatarios.isBlank()) {
            log.warn("Envio de e-mail ativado, mas nenhum destinatário foi configurado em app.email.destinatarios.");
            return;
        }

        try {
            SimpleMailMessage mensagem = new SimpleMailMessage();
            mensagem.setFrom(remetente);
            mensagem.setTo(obterDestinatarios());
            mensagem.setSubject("Rateio de gás calculado - " + rateio.getMesReferencia());
            mensagem.setText(montarCorpoEmail(rateio));

            javaMailSender.send(mensagem);

            log.info("E-mail de rateio enviado com sucesso para o mês {}.", rateio.getMesReferencia());
        } catch (MailException exception) {
            log.error("Falha ao enviar e-mail de rateio para o mês {}.", rateio.getMesReferencia(), exception);
        }
    }

    private String[] obterDestinatarios() {
        return Arrays.stream(destinatarios.split(","))
                .map(String::trim)
                .filter(destinatario -> !destinatario.isBlank())
                .toArray(String[]::new);
    }

    private String montarCorpoEmail(Rateio rateio) {
        StringBuilder corpo = new StringBuilder();

        corpo.append("Rateio de gás calculado\n\n");
        corpo.append("Mês de referência: ").append(rateio.getMesReferencia()).append("\n\n");

        corpo.append("Resumo geral\n");
        corpo.append("------------------------------\n");
        corpo.append("Valor total da conta: ").append(formatarMoeda(rateio.getValorTotalConta())).append("\n");
        corpo.append("Consumo do medidor principal: ").append(formatarNumero(rateio.getConsumoMedidorPrincipal())).append("\n");
        corpo.append("Consumo total dos secundários: ").append(formatarNumero(rateio.getConsumoTotalSecundario())).append("\n");
        corpo.append("Diferença de consumo: ").append(formatarNumero(rateio.getDiferencaConsumo())).append("\n\n");

        corpo.append("Divisão por torre\n");
        corpo.append("------------------------------\n");

        for (ItemRateio item : rateio.getItens()) {
            corpo.append(item.getNomeTorre()).append("\n");
            corpo.append("Consumo: ").append(formatarNumero(item.getConsumo())).append("\n");
            corpo.append("Percentual: ").append(formatarPercentual(item.getPercentual())).append("\n");
            corpo.append("Valor rateado: ").append(formatarMoeda(item.getValorRateado())).append("\n\n");
        }

        corpo.append("Este e-mail foi gerado automaticamente pelo Sistema de Rateio de Gás Condominial.\n");

        return corpo.toString();
    }

    private String formatarMoeda(BigDecimal valor) {
        return NumberFormat
                .getCurrencyInstance(Locale.of("pt", "BR"))
                .format(valor);
    }

    private String formatarNumero(BigDecimal valor) {
        return NumberFormat
                .getNumberInstance(Locale.of("pt", "BR"))
                .format(valor);
    }

    private String formatarPercentual(BigDecimal valor) {
        return NumberFormat
                .getNumberInstance(Locale.of("pt", "BR"))
                .format(valor) + "%";
    }
}
