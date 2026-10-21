package br.com.faculdadedonaduzzi.dataservice.service;

import br.com.faculdadedonaduzzi.dataservice.model.FishAnalysisEntity;
import br.com.faculdadedonaduzzi.dataservice.repository.FishRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class FishVisionService {

    @Autowired
    private FishRepository fishRepository;
    private final String PYTHON_INTERPRETER = "python3";
    private final String SCRIPT_PATH = "/app/scripts/predict.py";

    public String processarAnalise(MultipartFile imagemSerClassificadaPeloPython){
        //Crio uma entidade que será persistida no banco de dados
        FishAnalysisEntity fishAnalysisEntity = new FishAnalysisEntity();
        Path path = null;
        try {
            path = Files.createTempFile("fish_", ".jpg");
            //transferindo o arquivo da imagem para o novo arquivo temporário criado
            imagemSerClassificadaPeloPython.transferTo(path.toFile());
            //criando o processo oculto do python que irá chamar o interpretador
            ProcessBuilder pb = new ProcessBuilder(
                    // python predict.py "caminho da imagem a ser processada criada na temp"
                    PYTHON_INTERPRETER, SCRIPT_PATH, path.toString()
            );
            //unificando o stdout e o stderr
            pb.redirectErrorStream(true);
            // agora sim VAAAAAAAAI
            Process p = pb.start();
            //Abri um buffer read de um inputstreamReader do meu processo
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            // apendeia no stringBuilder
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            int codigoSaida = p.waitFor();
            // higiene do servidore senão ZERO SSD in the future
            Files.deleteIfExists(path);
            //seto o nome do arquivo como nome da imagem que foi avaliada
            fishAnalysisEntity.setNome(path.getFileName().toString());
            String resultado = sb.toString().trim();
            if (resultado.equals("FRESCO") || resultado.equals("ESTRAGADO")) {
                //setando o valor que o modelo retornou na entidade que vai para o banco
                fishAnalysisEntity.setAvaliacao(resultado);
                fishRepository.save(fishAnalysisEntity);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return fishAnalysisEntity.getId() != null? fishAnalysisEntity.getAvaliacao() : null;
    }
}
