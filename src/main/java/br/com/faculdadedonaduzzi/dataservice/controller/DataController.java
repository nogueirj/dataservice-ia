package br.com.faculdadedonaduzzi.dataservice.controller;

import br.com.faculdadedonaduzzi.dataservice.dto.MathResponse;
import br.com.faculdadedonaduzzi.dataservice.dto.SpamCheckRequest;
import br.com.faculdadedonaduzzi.dataservice.dto.SpamCheckResponse;
import br.com.faculdadedonaduzzi.dataservice.service.MathService;
import br.com.faculdadedonaduzzi.dataservice.service.PredictionService;
import br.com.faculdadedonaduzzi.dataservice.service.SpamDetectorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Tag(name = "Data Controller", description = "controller responsável por processar requisições de classificação de spam")
@RestController
@RequestMapping("/api/v1")
public class DataController {

    private PredictionService servicoDePredicao;
    private MathService servicoDeMath;
    private SpamDetectorService servicoDeSpam;


    public DataController(PredictionService predictionService, SpamDetectorService spam) {
        this.servicoDePredicao = predictionService;
//        this.servicoDeMath = mathService;
        this.servicoDeSpam = spam;
    }


    @GetMapping("status")
    public String checkStatus(){
        return "Microserviço de Dados operando com sucesso "
                + "Servidor ativo: " + LocalDateTime.now();
    }

    @GetMapping("/predict")
    public String getPrediction(@RequestParam("id") String customerId,
                                @RequestParam("amount") double amount) {
        double score = servicoDePredicao.calculateFraudRisk(customerId, amount);
        return String.format("Risco de Fraude para %s é: %.2f%%", customerId,
                score * 100);
    }

    @GetMapping("normalize")
    public MathResponse normalize(@RequestParam double val1,
                                  @RequestParam double val2,
                                  @RequestParam double val3) {
        return null;
    }

    @Operation(summary = "Classificar mensagem", description = "Recebe o conteúdo de uma mensagem e retorna se é spam ou não, junto com a pontuação de spam.")
    @ApiResponse(responseCode = "200", description = "Requisição processada com sucesso, retornando a classificação de spam.")
    @ApiResponse(responseCode = "400", description = "Requisição inválida, por exemplo, se o conteúdo da mensagem estiver ausente.")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor ao processar a requisição.")
    @ApiResponse(responseCode = "206", description = "Requisições com provaveis spam reportados.")
    @ApiResponse(responseCode = "300", description = "Alteração de recurso")
    @PostMapping("/classify")
    public ResponseEntity<SpamCheckResponse> classify(@RequestBody SpamCheckRequest request){
        SpamCheckResponse respostaDaApi = servicoDeSpam.spamCheck(request.messageContent());
        if (respostaDaApi.isBlocked()){
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).body(respostaDaApi);
        }
        return ResponseEntity.accepted().body(respostaDaApi);
    }

}
