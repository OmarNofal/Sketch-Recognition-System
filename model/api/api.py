from flask import Flask, request, jsonify
import prediction
from time import perf_counter

app = Flask("api")


@app.route('/predict', methods=['POST'])
def predict():

    print("---------- New Request ---------\n\n")

    data = request.data
    version = int(request.args['version'])

    ctime = perf_counter()

    response = prediction.predict(data, version)

    print(f"Whole prediction took {(perf_counter() - ctime) * 1000}ms")

    return jsonify(response)


app.run()
