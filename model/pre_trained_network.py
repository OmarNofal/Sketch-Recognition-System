from keras.applications import VGG16
from keras.models import Model
from keras.layers import Dense, Flatten
from keras.optimizers import Adam


def get_compiled_model() -> Model:

    vgg = VGG16(weights='imagenet', include_top=False, input_shape=(224, 224, 3))

    for layer in vgg.layers:
        layer.trainable = False

    x = Flatten()(vgg.output)
    predictions = Dense(125, activation='softmax')(x)

    model = Model(inputs=vgg.inputs, outputs=predictions)
    model.compile('adam', 'categorical_crossentropy', metrics=['accuracy'])
    return model
