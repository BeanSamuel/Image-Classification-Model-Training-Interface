import argparse
import threading
import tensorflow as tf
from tensorflow.keras.preprocessing.image import ImageDataGenerator
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import Conv2D, MaxPooling2D, Flatten, Dense
import os

class ProgressCallback(tf.keras.callbacks.Callback):
    def __init__(self, total_epochs):
        super(ProgressCallback, self).__init__()
        self.total_epochs = total_epochs

    def on_epoch_end(self, epoch, logs=None):
        progress = (epoch + 1) / self.total_epochs
        print(f'Epoch {epoch+1}/{self.total_epochs}')
        print(f'Progress: {progress * 100:.2f}%')

def create_model(class_num):
    model = Sequential()
    model.add(Conv2D(32, (3, 3), activation='relu', input_shape=(32, 32, 3)))
    model.add(MaxPooling2D((2, 2)))
    model.add(Conv2D(64, (3, 3), activation='relu'))
    model.add(MaxPooling2D((2, 2)))
    model.add(Conv2D(64, (3, 3), activation='relu'))
    model.add(Flatten())
    model.add(Dense(64, activation='relu'))
    model.add(Dense(class_num, activation='softmax'))
    return model

def train_model(epochs, learning_rate, batch_size, data_dir, save_path):
    if os.path.exists(save_path):
        os.remove(save_path)
    train_datagen = ImageDataGenerator(
        rescale=1./255,
        rotation_range=15,
        width_shift_range=0.1,
        height_shift_range=0.1,
        horizontal_flip=True
    )

    subdir = [name for name in os.listdir(data_dir) if os.path.isdir(os.path.join(data_dir, name))]
    class_num = len(subdir)

    # 载入数据集
    train_generator = train_datagen.flow_from_directory(
        data_dir,
        target_size=(32, 32),
        batch_size=batch_size,
        class_mode='categorical',
        shuffle=True
    )

    model = create_model(class_num)

    optimizer = tf.keras.optimizers.Adam(learning_rate=learning_rate)
    loss_fn = tf.keras.losses.CategoricalCrossentropy()

    model.compile(optimizer=optimizer, loss=loss_fn, metrics=['accuracy'])

    progress_callback = ProgressCallback(epochs)

    training_thread = threading.Thread(target=train_model_thread, args=(model, train_generator, epochs, progress_callback, save_path))
    training_thread.start()

def train_model_thread(model, train_generator, epochs, progress_callback, save_path):
    model.fit(
        train_generator,
        epochs=epochs,
        callbacks=[progress_callback]
    )

    model.save(save_path,save_format='h5')

if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('--epochs', type=int, default=10)
    parser.add_argument('--learning_rate', type=float, default=0.001)
    parser.add_argument('--batch_size', type=int, default=32)
    parser.add_argument('--data_dir', type=str, default='D:\\NCU\\大一下\\計算機實習\\FInalProject\\FinalProject\\src\\main\\resources\\data\\TrainingFile')
    parser.add_argument('--save_path', type=str, default='D:\\NCU\\大一下\\計算機實習\\FInalProject\\FinalProject\\src\\main\\resources\\com\\example\\model\\model.h5')
    args = parser.parse_args()

    train_model(args.epochs, args.learning_rate, args.batch_size, args.data_dir, args.save_path)
