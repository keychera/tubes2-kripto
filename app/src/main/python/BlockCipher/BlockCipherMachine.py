class BlockCipherMachine():
    def __init__(self, mode, cryptor):
        self.mode = mode
        self.cryptor = cryptor
        self.block_list = list()
        self.result_list = list()
        self.cycle = 0

    def set_block_list(self, bytes_input, block_size):
        print(bytes_input)
        print(type(bytes_input))
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

    def reset(self):
        self.result_list = list()
        self.cycle = 0
        