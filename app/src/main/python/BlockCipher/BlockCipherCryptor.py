import random

from BlockCipher.BlockCipherStructure import FeistelUnit
from BlockCipher.BlockCipherStructure import Permutor
from BlockCipher.BlockCipherStructure import Pyramid

class KeyManager():
    # key is array of byte, size = 8 (64 bit)
    def __init__(self, key):
        self.external_key = int.from_bytes(key, 'big')
        random.seed(self.external_key)
        self.round_keys = random.sample(range(256), 20)

    def get_round_key(self, round_number):
        return self.round_keys[round_number]

class Cipher(object):
    def __init__(self, key):
        self.key_manager = KeyManager(key)
        permutor = Permutor()
        pyramid = Pyramid(self.key_manager.external_key)
        self.feistel_unit = FeistelUnit(permutor, pyramid)
    
    def config_round(self, round_number):
        self.feistel_unit.set_round_key(self.key_manager.get_round_key(round_number))
        self.feistel_unit.set_ordering()

    def encrypt(self, array_bytes):
        result = array_bytes
        for i in list(range(20)):
            self.config_round(i)
            result = self.feistel_unit.run(result, encrypt=True)
        return result
    
    def decrypt(self, array_bytes):
        result = array_bytes
        for i in list(reversed(range(20))):
            self.config_round(i)
            result = self.feistel_unit.run(result, encrypt=False)
        return result
