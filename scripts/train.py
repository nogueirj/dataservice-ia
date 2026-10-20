import os
import cv2
import numpy as np
import joblib
from skimage import feature
from sklearn.linear_model import LogisticRegression
from skl2onnx import convert_sklearn
from skl2onnx.common.data_types import FloatTensorType

# Configurações do LBP (Raio 2, 16 pontos)
RADIUS = 2
N_POINTS = 8 * RADIUS  # 16 pontos

def extract_lbp_features(image_path):
    # Carrega a imagem em escala de cinza
    img = cv2.imread(image_path, cv2.IMREAD_GRAYSCALE)
    if img is None:
        raise ValueError(f"Erro ao carregar imagem: {image_path}")

    # Extrai o LBP uniforme
    lbp = feature.local_binary_pattern(img, N_POINTS, RADIUS, method="uniform")

    # Calcula o histograma (as características para a Regressão Logística)
    # Para LBP uniforme com P pontos, os valores vão de 0 a P+2 (total P+3 bins)
    hist, _ = np.histogram(lbp.ravel(), bins=np.arange(0, N_POINTS + 3), range=(0, N_POINTS + 2))
    hist = hist.astype("float")
    hist /= (hist.sum() + 1e-7)  # Normalização
    return hist

def train():
    X = []
    y = []

    # Pastas com as imagens
    classes = {
        "/Users/julionogueira/Downloads/FishFillet 3/Fresco": 0,
        "/Users/julionogueira/Downloads/FishFillet 3/Estragado": 1
    }

    for folder, label in classes.items():
        if not os.path.exists(folder):
            print(f"Pasta não encontrada: {folder}")
            continue
        for filename in os.listdir(folder):
            path = os.path.join(folder, filename)
            try:
                features = extract_lbp_features(path)
                X.append(features)
                y.append(label)
                print(f"Processado: {path}")
            except Exception as e:
                print(f"Erro em {path}: {e}")

    if not X:
        print("Dataset não encontrado. Treinamento abortado.")
        return

    X = np.array(X)
    y = np.array(y)
    print(f"Total de amostras: {X.shape[0]}, Features: {X.shape[1]}")

    # Treina a Regressão Logística
    print("Treinando modelo...")
    model = LogisticRegression(random_state=42, max_iter=1000)
    model.fit(X, y)

    # Salva o modelo treinado em formato pickle
    joblib.dump(model, 'fish_cv_model.pkl')
    print("Modelo salvo como fish_cv_model.pkl")

    # ===== CONVERSÃO PARA ONNX =====
    print("Convertendo modelo para ONNX...")
    # Número de features (deve ser o mesmo que X.shape[1])
    n_features = X.shape[1]
    initial_type = [('float_input', FloatTensorType([None, n_features]))]

    # Converter o modelo (LogisticRegression) para ONNX
    onx = convert_sklearn(model, initial_types=initial_type, target_opset=12)

    # Salvar arquivo ONNX
    onnx_filename = "fish_lr_model.onnx"
    with open(onnx_filename, "wb") as f:
        f.write(onx.SerializeToString())
    print(f"Modelo ONNX salvo como {onnx_filename}")

    # ===== VERIFICAÇÃO DE EQUIVALÊNCIA (OPCIONAL) =====
    try:
        import onnxruntime as ort
        from sklearn.metrics import accuracy_score

        # Selecionar algumas amostras (pode ser o próprio X)
        X_sample = X.astype(np.float32)
        y_sklearn = model.predict(X_sample)

        sess = ort.InferenceSession(onnx_filename)
        input_name = sess.get_inputs()[0].name
        ort_outs = sess.run(None, {input_name: X_sample})
        # A primeira saída geralmente é o rótulo
        y_onnx = ort_outs[0].flatten()
        acc = accuracy_score(y_sklearn, y_onnx)
        print(f"Acurácia ONNX vs sklearn: {acc:.2f} (deve ser 1.0)")
        if acc == 1.0:
            print("OK: Modelos equivalentes.")
        else:
            print("ATENÇÃO: Diferenças encontradas.")
    except ImportError:
        print("onnxruntime não instalado, pulando verificação.")

if __name__ == "__main__":
    train()