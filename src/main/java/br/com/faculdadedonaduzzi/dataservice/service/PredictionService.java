package br.com.faculdadedonaduzzi.dataservice.service;

import org.springframework.stereotype.Service;
import java.util.Random;

@Service // Essencial para registrar o Bean
public class PredictionService {

    // método de predição, ele recebe uma customerId e o montante da transação
    public double calculateFraudRisk(String customerId,
                                     double transactionAmount) {
        System.out.println("Processando dados do cliente: " + customerId);
        double riskScore = (transactionAmount / 10000.0)
                + new Random().nextDouble(0.5);
        return Math.min(riskScore, 1.0);
    }
}