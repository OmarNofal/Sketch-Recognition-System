import network
from keras.callbacks import \
    EarlyStopping, ReduceLROnPlateau, CSVLogger, TensorBoard, ModelCheckpoint, LearningRateScheduler
from data import get_dataset
import pre_trained_network


def scheduler(epoch, lr):
    if epoch < 10:
        return lr
    else:
        if epoch % 10 == 0:
            return lr * 0.85
        else:
            return lr


model = pre_trained_network.get_compiled_model()


es = EarlyStopping(
    monitor='val_accuracy',
    patience=50
)

rl = ReduceLROnPlateau(
    monitor='val_accuracy',
    patience=30,
    factor=0.6
)

mc = ModelCheckpoint(
    './model/ModelCheckpoint',
    monitor='val_accuracy',
    save_best_only=True,
)

lrs = LearningRateScheduler(scheduler)

csvLogger = CSVLogger('./logs/history.csv')

tb = TensorBoard(histogram_freq=1, log_dir='./logs/training_logs')


t_data, v_data = get_dataset()

model.fit(
    x=t_data,
    epochs=400,
    callbacks=[es, mc, csvLogger, tb, rl, lrs],
    validation_data=v_data
)