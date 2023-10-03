from keras.layers import Dense, BatchNormalization, Conv2D, Input, Flatten, MaxPool2D, Dropout, Activation, Normalization
from keras.optimizers import Adam, SGD
from keras.models import Sequential
from keras.initializers.initializers_v2 import RandomUniform, GlorotUniform


def get_compiled_model():
    model = Sequential()
    model.add(Input((256, 256, 1)))
    model.add(Normalization(mean=0, variance=255**2))  # data between 0, 1
    model.add(
        Conv2D(
            kernel_size=(8, 8),
            filters=64,
            strides=(2, 2),
            activation='relu',
            kernel_initializer=GlorotUniform(),
            #kernel_regularizer='l2',
            bias_regularizer='l2'
        )
    )
    model.add(BatchNormalization())
    model.add(
        MaxPool2D(
            pool_size=(2, 2),
            strides=(1, 1),
        )
    )
    model.add(BatchNormalization())
    model.add(
        Conv2D(
            kernel_size=(2, 2),
            strides=(2, 2),
            filters=256,
            activation='relu',
            kernel_initializer=GlorotUniform(),
            kernel_regularizer='l2'
        )
    )
    model.add(BatchNormalization())
    model.add(
        MaxPool2D(
            pool_size=(2, 2),
            strides=(2, 2),
        )
    )
    model.add(BatchNormalization())
    model.add(
        Conv2D(
            kernel_size=(2, 2),
            filters=256,
            strides=(3, 3),
            activation='relu',
            kernel_initializer=GlorotUniform(),
            kernel_regularizer='l2'
        )
    )
    model.add(BatchNormalization())
    model.add(
        MaxPool2D(
            pool_size=(2, 2),
            strides=(1, 1)
        )
    )
    model.add(BatchNormalization())
    model.add(
        Conv2D(
            kernel_size=(3, 3),
            filters=256,
            strides=(2, 2),
            activation='relu',
            kernel_initializer=GlorotUniform(),
            kernel_regularizer='l2'
        )
    )
    model.add(Flatten())
    model.add(BatchNormalization())
    model.add(
        Dense(units=1024, activation='sigmoid', kernel_initializer=GlorotUniform(),
              bias_regularizer='l2'
              #kernel_regularizer='l2'
              )
    )
    model.add(
        Dropout(0.4)
    )
    model.add(
        Dense(
            units=125,
            activation='softmax',
            kernel_initializer=RandomUniform(
                minval=-0.35, maxval=0.35
            ),
            #kernel_regularizer='l1'
        )
    )

    model.compile(
        optimizer=Adam(learning_rate=0.0021),
        loss='categorical_crossentropy',
        metrics=['accuracy']
    )

    return model


if __name__ == "__main__":
    print(get_compiled_model().summary())
