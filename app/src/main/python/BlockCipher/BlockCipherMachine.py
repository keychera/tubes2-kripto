from functools import reduce
import base64

class BlockCipherMachine():
    def __init__(self, mode, cryptor):
        self.mode = mode
        self.cryptor = cryptor
        self.block_list = list()
        self.result_list = list()
        self.cycle = 0

    def set_block_list_from_encrypted_string(self, string_input, block_size):
        base64string = str.encode(string_input,'utf-8')
        block_bytes = base64.b64decode(base64string) 
        self.set_block_list(block_bytes, block_size)
        

    def set_block_list(self, bytes_input, block_size):
        self.block_list = list()
        temp = bytes_input
        # padding with 0 at the end
        while (len(temp) % block_size > 0):
            temp = temp + bytes([0])

        for i in range(0, len(temp), block_size):
            block = [temp[i] for i in range(i, i + block_size)]
            self.block_list.append(block)

    def run(self, is_encryption):
        for i in range(0, len(self.block_list)):
            self.cycle = i
            self.mode.operate(self, is_encryption)

    def get_b64_encoded_string_result(self):
        result = reduce(lambda i, j: i + j, self.result_list)
        result = base64.b64encode(bytearray(result))
        return result.decode('utf-8')
    
    def get_string_result(self):
        result = reduce(lambda i, j: i + j, self.result_list)
        result = bytes(result)
        return result.decode('utf-8')

    def reset(self):
        self.result_list = list()
        self.cycle = 0
        