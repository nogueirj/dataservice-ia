package br.com.faculdadedonaduzzi.dataservice.service;

import br.com.faculdadedonaduzzi.dataservice.dto.SpamCheckResponse;
import org.springframework.stereotype.Service;

@Service
public class SpamDetectorService {

    public SpamCheckResponse spamCheck(String messageContent) {
        if (messageContent == null || messageContent.isBlank()) {
            return new SpamCheckResponse("ERRO", 0.0, false);
        }
        double score = 0.0;
        String lower = messageContent.toLowerCase();
        if (lower.contains("grátis")) score += 0.4;
        if (lower.contains("dinheiro")) score += 0.3;
        if (lower.contains("clique aqui")) score += 0.5;
        if (lower.contains("promoção")) score += 0.2;

        score = Math.min(score, 1.0);

        boolean isSpam = score > 0.7;

        return new SpamCheckResponse( isSpam? "SPAM" : "HAM", score, isSpam);
    }
}
