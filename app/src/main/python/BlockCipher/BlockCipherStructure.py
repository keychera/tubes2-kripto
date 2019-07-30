import random

# S-box, map value from domain [0x00, 0xff] to range [0x00, 0xff]
class SubsBox():
    # entry is array of int, 256 elements, each element is in [0x00, 0xff]
    def __init__(self, entry):
        self.table = entry
    
    # return subtitution value for array_bytes
    # each array_bytes element is in [0x00, 0xff]
    def substitute(self, array_bytes):
        return [self.table[i] for i in array_bytes]

# Pyramid has 10 S-box, arranged to 4 layers with a certain ordering
# First layer has 1 box, second layer has 2 boxes, etc
# Pyramid subtitute a 4 bytes value to another 4 bytes value
class Pyramid():
    def __init__(self, seed):
        self.boxes = []
        random.seed(seed)
        for i in range(10):
            self.boxes.append(SubsBox(random.sample(range(256), 256)))
        
        self.ordering = range(10)

    def get_box(self, number):
        return self.boxes[self.ordering[number]]

    # ordering is array with 10 elements, each element is in [0, 9]
    def set_ordering(self, ordering):
        self.ordering = ordering

    # array_bytes size must be 4 (32 bit)
    def substitute(self, array_bytes):
        result = array_bytes

        # layer 1, substitute 4 bytes using box 0
        result = self.get_box(0).substitute(result)

        # layer 2, substitute 2 bytes using box 1, 2 bytes using box 2
        temp = self.get_box(1).substitute(result[:2])
        temp = temp + self.get_box(2).substitute(result[2:])
        result = temp
        
        # layer 3, substitute 1 byte using box 3, 2 bytes using box 4, 1 byte using box 5
        temp = self.get_box(3).substitute(result[0:1])
        temp = temp + self.get_box(4).substitute(result[1:3])
        temp = temp + self.get_box(5).substitute(result[3:])
        result = temp

        # layer 4, substitute 1 byte using box 6, 1 byte using box 7,
        # 1 byte using box 8, 1 byte using box 9
        temp = self.get_box(6).substitute(result[0:1])
        temp = temp + self.get_box(7).substitute(result[1:2])
        temp = temp + self.get_box(8).substitute(result[2:3])
        temp = temp + self.get_box(9).substitute(result[3:])
        result = temp

        return result

# Permutor can shuffle and inverse shuffle array of 8 bytes
class Permutor():
    def __init__(self):
        self.ordering = range(8)
    
    def set_ordering(self, ordering):
        self.ordering = ordering

    def permute(self, array_bytes):
        return [array_bytes[i] for i in self.ordering]
    
    def inverse(self, array_bytes):
        return [array_bytes[self.ordering.index(i)] for i in range(len(array_bytes))]

class FeistelUnit():
    def __init__(self, permutor, pyramid):
        self.pyramid = pyramid
        self.permutor = permutor
    
    def set_round_key(self, round_key):
        self.round_key = round_key

    def set_ordering(self):
        random.seed(self.round_key)
        permutor_ordering = random.sample(range(8), 8)
        self.permutor.set_ordering(permutor_ordering)
        pyramid_ordering = random.sample(range(10), 10)
        self.pyramid.set_ordering(pyramid_ordering)
    
    def run(self, array_bytes, encrypt=True):
        result = array_bytes
        
        if encrypt:
            result = self.permutor.permute(result)

        temp = self.pyramid.substitute(result[4:])
        temp = [i ^ j for i, j in zip(result[:4], temp)] + result[4:]
        result = temp

        if not encrypt:
            result = self.permutor.inverse(result)

        return result
    