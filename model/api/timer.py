from time import perf_counter


class Timer(object):

    def __init__(self, name):
        self.time = 0
        self.name = name

    def __enter__(self):
        self.time = perf_counter()

    def __exit__(self, exc_type, exc_val, exc_tb):
        elapsed = perf_counter() - self.time
        print(f"{self.name} took {elapsed * 1000}ms")
