from abc import ABC, abstractmethod

def list_xor(list1, list2):
    return [i ^ j for i, j in zip(list1, list2)]

class Mode(ABC):
    def __init__(self):
        self.is_encryption = True
        self.machine = None
    
    @abstractmethod
    def operate(self, machine, is_encryption):
        pass

class ECBMode(Mode):
    def operate(self, machine, is_encryption):
        cycle = machine.cycle
        if is_encryption:
            machine.result_list.append(machine.cryptor.encrypt(machine.block_list[cycle]))
        else:
            machine.result_list.append(machine.cryptor.decrypt(machine.block_list[cycle]))

class CBCMode(Mode):
    def __init__(self, IV):
        self.IV = IV
        super().__init__()
    
    def operate(self, machine, is_encryption):
        cycle = machine.cycle
        if is_encryption:
            if (cycle == 0):
                or_product = list_xor(machine.block_list[cycle], self.IV)
            else:
                or_product = list_xor(machine.block_list[cycle], machine.result_list[cycle - 1])
            machine.result_list.append(machine.cryptor.encrypt(or_product))
        else:
            decrypted_block = machine.cryptor.decrypt(machine.block_list[cycle]) 
            if (cycle == 0):
                result = list_xor(decrypted_block, self.IV)
            else:
                result = list_xor(decrypted_block, machine.block_list[cycle - 1])
            machine.result_list.append(result)


class CFBMode(Mode):
    def __init__(self, IV):
        self.IV = IV
        super().__init__()
    
    def operate(self, machine, is_encryption):
        cycle = machine.cycle
        if is_encryption:
            if (cycle == 0):
                result = list_xor(machine.cryptor.encrypt(self.IV), machine.block_list[cycle])
            else:
                result = list_xor(machine.cryptor.encrypt(machine.result_list[cycle - 1]), machine.block_list[cycle])
            machine.result_list.append(result)
        else:
            if (cycle == 0):
                result = list_xor(machine.cryptor.encrypt(self.IV), machine.block_list[cycle])
            else:
                result = list_xor(machine.cryptor.encrypt(machine.block_list[cycle - 1]), machine.block_list[cycle])
            machine.result_list.append(result)

class OFBMode(Mode):
    def __init__(self, IV):
        self.IV = IV
        super().__init__()

    def operate(self, machine, is_encryption):
        cycle = machine.cycle
        if (cycle == 0):
            self.output = machine.cryptor.encrypt(self.IV)
        else:
            self.output = machine.cryptor.encrypt(self.output)
        machine.result_list.append(list_xor(machine.block_list[cycle], self.output))

class CTRMode(Mode):
    def operate(self, machine, is_encryption):
        cycle = machine.cycle
        cycle_bytes = bytes([cycle])
        while(len(cycle_bytes) < 8):
            cycle_bytes = bytes([0]) + cycle_bytes
        cycle_bytes = [cycle_bytes[i] for i in range(len(cycle_bytes))]
        machine.result_list.append(list_xor(machine.block_list[cycle], machine.cryptor.decrypt(cycle_bytes)))
