package br.com.armandorodrigues.gasrateio.infrastructure.report;

import br.com.armandorodrigues.gasrateio.application.service.RateioEmailService;
import br.com.armandorodrigues.gasrateio.domain.model.ItemRateio;
import br.com.armandorodrigues.gasrateio.domain.model.Rateio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;

@Service
public class SmtpRateioEmailService implements RateioEmailService {

    private static final Logger logger = LoggerFactory.getLogger(SmtpRateioEmailService.class);

    private final JavaMailSender mailSender;
    private final boolean emailEnabled;
    private final String destinatarios;
    private final String remetente;

    public SmtpRateioEmailService(
            JavaMailSender mailSender,
            @Value("${app.email.enabled:false}") boolean emailEnabled,
            @Value("${app.email.destinatarios:}") String destinatarios,
            @Value("${app.email.remetente:gas-rateio@localhost}") String remetente
    ) {
        this.mailSender = mailSender;
        this.emailEnabled = emailEnabled;
        this.destinatarios = destinatarios;
        this.remetente = remetente;
    }

    @Override
    public void enviarRateioCalculado(Rateio rateio) {
        if (!emailEnabled) {
            logger.info("Envio de e-mail desativado. Rateio {} não será enviado por e-mail.", rateio.getMesReferencia());
            return;
        }

        if (destinatarios == null || destinatarios.trim().isEmpty()) {
            logger.warn("Nenhum destinatário configurado para envio do rateio por e-mail.");
            return;
        }

        try {
            SimpleMailMessage mensagem = new SimpleMailMessage();
            mensagem.setFrom(remetente);
            mensagem.setTo(obterDestinatarios());
            mensagem.setSubject("Rateio de gás calculado - " + rateio.getMesReferencia());
            mensagem.setText(montarCorpoEmail(rateio));

            mailSender.send(mensagem);

            logger.info("E-mail de rateio enviado com sucesso para o mês {}.", rateio.getMesReferencia());
        } catch (MailException exception) {
            logger.error(
                    "Falha ao enviar e-mail do rateio {}. O rateio foi calculado normalmente.",
                    rateio.getMesReferencia(),
                    exception
            );
        }
    }

    private String[] obterDestinatarios() {
        return Arrays.stream(destinatarios.split(","))
                .map(String::trim)
                .filter(email -> !email.isEmpty())
                .toArray(String[]::new);
    }

    private String montarCorpoEmail(Rateio rateio) {
        StringBuilder corpo = new StringBuilder();

        corpo.append("Rateio de gás calculado\n\n");
        corpo.append("Mês de referência: ").append(rateio.getMesReferencia()).append("\n\n");

        corpo.append("Resumo geral\n");
        corpo.append("Valor total da conta: ").append(formatarMoeda(rateio.getValorTotalConta())).append("\n");
        corpo.append("Consumo medidor principal: ").append(formatarNumero(rateio.getConsumoMedidorPrincipal())).append("\n");
        corpo.append("Consumo total secundário: ").append(formatarNumero(rateio.getConsumoTotalSecundario())).append("\n");
        corpo.append("Diferença de consumo: ").append(formatarNumero(rateio.getDiferencaConsumo())).append("\n\n");

        corpo.append("Divisão por torre\n");

        for (ItemRateio item : rateio.getItens()) {
            corpo.append("\n");
            corpo.append(item.getNomeTorre()).append("\n");
            corpo.append("Consumo: ").append(formatarNumero(item.getConsumo())).append("\n");
            corpo.append("Percentual: ").append(formatarPercentual(item.getPercentual())).append("\n");
            corpo.append("Valor rateado: ").append(formatarMoeda(item.getValorRateado())).append("\n");
        }

        corpo.append("\nEste e-mail foi gerado automaticamente pelo sistema GasRateio.");

        return corpo.toString();
    }

    private String formatarMoeda(BigDecimal valor) {
        return NumberFormat
                .getCurrencyInstance(new Locale("pt", "BR"))
                .format(valor);
    }

    private String formatarNumero(BigDecimal valor) {
        return NumberFormat
                .getNumberInstance(new Locale("pt", "BR"))
                .format(valor);
    }

    private String formatarPercentual(BigDecimal valor) {
        return NumberFormat
                .getNumberInstance(new Locale("pt", "BR"))
                .format(valor) + "%";
    }
}
