import sys
import cv2
import numpy as np
import joblib
from skimage import feature

RADIUS = 2
N_POINTS = 8 * RADIUS

def extract_lbp_features(image_path):
    img = cv2.imread(image_path, cv2.IMREAD_GRAYSCALE)
    lbp = feature.local_binary_pattern(img, N_POINTS, RADIUS, method="uniform")
    (hist, _) = np.histogram(lbp.ravel(), bins=np.arange(0, N_POINTS + 3), range=(0, N_POINTS + 2))
    hist = hist.astype("float")
    hist /= (hist.sum() + 1e-7)
    return hist

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("ERRO: Caminho da imagem não fornecido.")
        sys.exit(1)

    image_path = sys.argv[1]

    try:
        # Extrai features
        features = extract_lbp_features(image_path)

        # Carrega o modelo
        model = joblib.load('/app/scripts/fish_cv_model.pkl')

        # Predição
        prediction = model.predict([features])[0]

        # A saída que o Java vai ler
        if prediction == 0:
            print("FRESCO")
        else:
            print("ESTRAGADO")

    except Exception as e:
        print(f"ERRO: {str(e)}")
        sys.exit(1)