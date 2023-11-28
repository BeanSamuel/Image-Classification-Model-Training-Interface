import argparse
import tensorflow as tf
from tensorflow.keras.preprocessing.image import load_img, img_to_array
import numpy as np
import sys
import os

def load_model(model_path):
    return tf.keras.models.load_model(model_path)

def count_files_in_directory(directory):
    return len([name for name in os.listdir(directory) if os.path.isfile(os.path.join(directory, name))])

def predict(model, image_path, class_names):
    img = load_img("D:\\NCU\\大一下\\計算機實習\\FInalProject\\FinalProject\\src\\main\\resources\\data\\PredictFile\\"+image_path, target_size=(32, 32))
    img_array = img_to_array(img)
    img_array = img_array / 255.0 
    img_array = np.expand_dims(img_array, axis=0)

    predictions = model.predict(img_array)
    for i in range(len(predictions[0])):
        print(class_names[i] + ": " + str(predictions[0][i]))


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('--model_path', type=str, default='d:\\model.h5')
    parser.add_argument('--image_path', type=str, default='cat.4001.jpg')
    parser.add_argument('--class_names', nargs='+', type=str, default=['class1', 'class2'])
    args = parser.parse_args()

    model = load_model(args.model_path)

    encoding = sys.getfilesystemencoding()

    predict(model, args.image_path, args.class_names)
