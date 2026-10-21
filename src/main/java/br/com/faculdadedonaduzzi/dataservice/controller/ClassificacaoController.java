package br.com.faculdadedonaduzzi.dataservice.controller;

import br.com.faculdadedonaduzzi.dataservice.service.FishVisionService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;


@Tag(name = "Classificação Controller Peixes", description = "Controller responsável por processar requisições de classificação de spam")
@RestController()
@RequestMapping("api/classificao")
public class ClassificacaoController {

    @Autowired
    private FishVisionService fishVisionService;

    @PostMapping("classificar-peixes")
    @ApiResponse(description = "Sucesso", responseCode = "202")
    @ApiResponse(description = "Erro durante o processamento no Python", responseCode = "500")
    public ResponseEntity<String> classificar(
            @RequestParam("imagem") MultipartFile imagemSerClassificadaPeloPython) {

        String resultado =  fishVisionService.processarAnalise(imagemSerClassificadaPeloPython);

        if (resultado != null) {
            return ResponseEntity.ok(resultado);
        }

        return ResponseEntity.status(500).body("Erro no script python");
    }
}
