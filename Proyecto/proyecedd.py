import bisect
#import random, math

outputdebug = False 

def debug(msg):
    if outputdebug:
        print msg

class Node():
    def __init__(self, key, nombre, archivo):
        self.key = key
        self.nombre= nombre
        self.archivo= archivo
        self.left = None 
        self.right = None 




class AVLTree():
    def __init__(self, *args):
        self.node = None 
        self.height = -1  
        self.balance = 0; 
        
        if len(args) == 1: 
            for i in args[0]: 
                self.insert(i)
                
    def height(self):
        if self.node: 
            return self.node.height 
        else: 
            return 0 
    
    def is_leaf(self):
        return (self.height == 0) 
    
    def insert(self, key,nombre,archivo):
        tree = self.node
        
        newnode = Node(key,nombre,archivo)
        
        if tree == None:
            self.node = newnode 
            self.node.left = AVLTree() 
            self.node.right = AVLTree()
            debug("Inserted key [" + str(key) + "]")
        
        elif key <= tree.key: 
            self.node.left.insert(key,nombre,archivo)
            
        elif key >= tree.key: 
            self.node.right.insert(key,nombre,archivo)
        
        else: 
            debug("Key [" + str(newnode.key) + "] already in tree.")
            
        self.rebalance() 


        
    def rebalance(self):
        ''' 
        Rebalance a particular (sub)tree
        ''' 
        # key inserted. Let's check if we're balanced
        self.update_heights(False)
        self.update_balances(False)
        while self.balance < -1 or self.balance > 1: 
            if self.balance > 1:
                if self.node.left.balance < 0:  
                    self.node.left.lrotate() # we're in case II
                    self.update_heights()
                    self.update_balances()
                self.rrotate()
                self.update_heights()
                self.update_balances()
                
            if self.balance < -1:
                if self.node.right.balance > 0:  
                    self.node.right.rrotate() # we're in case III
                    self.update_heights()
                    self.update_balances()
                self.lrotate()
                self.update_heights()
                self.update_balances()


            
    def rrotate(self):
        # Rotate left pivoting on self
        debug ('Rotating ' + str(self.node.key) + ' right') 
        A = self.node 
        B = self.node.left.node 
        T = B.right.node 
        
        self.node = B 
        B.right.node = A 
        A.left.node = T 

    
    def lrotate(self):
        # Rotate left pivoting on self
        debug ('Rotating ' + str(self.node.key) + ' left') 
        A = self.node 
        B = self.node.right.node 
        T = B.left.node 
        
        self.node = B 
        B.left.node = A 
        A.right.node = T 
        
            
    def update_heights(self, recurse=True):
        if not self.node == None: 
            if recurse: 
                if self.node.left != None: 
                    self.node.left.update_heights()
                if self.node.right != None:
                    self.node.right.update_heights()
            
            self.height = max(self.node.left.height,
                              self.node.right.height) + 1 
        else: 
            self.height = -1 
            
    def update_balances(self, recurse=True):
        if not self.node == None: 
            if recurse: 
                if self.node.left != None: 
                    self.node.left.update_balances()
                if self.node.right != None:
                    self.node.right.update_balances()

            self.balance = self.node.left.height - self.node.right.height 
        else: 
            self.balance = 0 

    def delete(self, key):
        # debug("Trying to delete at node: " + str(self.node.key))
        if self.node != None: 
            if self.node.key == key: 
                debug("Deleting ... " + str(key))  
                if self.node.left.node == None and self.node.right.node == None:
                    self.node = None # leaves can be killed at will 
                # if only one subtree, take that 
                elif self.node.left.node == None: 
                    self.node = self.node.right.node
                elif self.node.right.node == None: 
                    self.node = self.node.left.node
                
                # worst-case: both children present. Find logical successor
                else:  
                    replacement = self.logical_successor(self.node)
                    if replacement != None: # sanity check 
                        debug("Found replacement for " + str(key) + " -> " + str(replacement.key))  
                        self.node.key = replacement.key 
                        
                        # replaced. Now delete the key from right child 
                        self.node.right.delete(replacement.key)
                    
                self.rebalance()
                return  
            elif key < self.node.key: 
                self.node.left.delete(key)  
            elif key > self.node.key: 
                self.node.right.delete(key)
                        
            self.rebalance()
        else: 
            return 

    def logical_predecessor(self, node):
        ''' 
        Find the biggest valued node in LEFT child
        ''' 
        node = node.left.node 
        if node != None: 
            while node.right != None:
                if node.right.node == None: 
                    return node 
                else: 
                    node = node.right.node  
        return node 
    
    def logical_successor(self, node):
        ''' 
        Find the smallese valued node in RIGHT child
        ''' 
        node = node.right.node  
        if node != None: # just a sanity check  
            
            while node.left != None:
                debug("LS: traversing: " + str(node.key))
                if node.left.node == None: 
                    return node 
                else: 
                    node = node.left.node  
        return node 

    def check_balanced(self):
        if self == None or self.node == None: 
            return True
        
        # We always need to make sure we are balanced 
        self.update_heights()
        self.update_balances()
        return ((abs(self.balance) < 2) and self.node.left.check_balanced() and self.node.right.check_balanced())  
        
    def inorder_traverse(self):
        if self.node == None:
            return [] 
        
        inlist = [] 
        l = self.node.left.inorder_traverse()
        for i in l: 
            inlist.append(i) 

        inlist.append(self.node.key)

        l = self.node.right.inorder_traverse()
        for i in l: 
            inlist.append(i) 
    
        return inlist 

    def display(self, level, pref,archivo):
        '''
        Display the whole tree. Uses recursive def.
        TODO: create a better display using breadth-first search
        '''
             
        self.update_heights()  # Must update heights before balances 
        self.update_balances()
        if(self.node != None): 
            padre=  self.node.nombre 
            if pref != '':
                archivo.writelines(str(pref)+ str(self.node.nombre)+";") 
            #print pref, str(self.node.key) + ";"    
            if self.node.left != None: 
                self.node.left.display(level + 1, str(padre)+"->",archivo)
            if self.node.left != None:
                self.node.right.display(level + 1, str(padre)+"->",archivo)

            
    

    def dibujarAvl(self):
        archivo=open('avl.dot', 'w')
        archivo.write('digraph G{\n')
        archivo.write("node [fontname=\"Arial\"];\n");
        #archivo.write("rankdir = TD;\n");
        self.display(0,'', archivo)
        archivo.write('}')
        archivo.close()            
           
        

              
class NodoB(object):
    def __init__(self, idNombre=None, nombreCarpeta=None, direccion=None, arbol= None):
        self.idNombre = idNombre
        self.nombreCarpeta = nombreCarpeta
        self.nodoAVL = AVLTree()
        #self.arbol = ArbolB()
        self.direccion = direccion
        self.arbol= arbol
      
        

class Pagina(object): 
    def __init__(self, ramas=[0,0,0,0,0], claves=[0,0,0,0], cuentas=0):     
        self.ramas = ramas
        self.claves = claves
        self.cuentas = cuentas

class ArbolB(object):
    def __init__(self):
        self.inicio = Pagina()
        self.inicio2 = Pagina()
        self.inserta = NodoB()
        #self.nodoAVL=AVLTree()
        self.enlace = Pagina()
        self.pivote = False
        self.existe = False
        self.existe2 = False
    
    
              
        
        
    #Crea el Nodo del Arbol B
    def crearNodoInsertar(self, idNombre, nombreCarpeta, direccion,arbol):
        nodob = NodoB(idNombre, nombreCarpeta, direccion,arbol)
        self.InsertarArbolB(nodob, self.inicio)
    def avlre(self):
        nodo = self.inserta

        return nodo.nodoAVL
        
   
    #Inserta el nodo al Arbol B La clave es el Nodo y la raiz la Pagina
    def InsertarArbolB(self, clave, raiz):
        self.agregar(clave, raiz)
        if(self.pivote == True):
            self.inicio = Pagina(ramas=[None,None,None,None,None], claves=[None,None,None,None], cuentas=0)
            self.inicio.cuentas = 1
            self.inicio.claves[0] = self.inserta
            self.inicio.ramas[0] = raiz
            self.inicio.ramas[1] = self.enlace
            
            
    #Agregar al Arbol, Balanceando el arbol por Id
    def agregar(self, clave, raiz):
        pos = 0              
        self.pivote = False; 
        
        vacioBol = self.vacio(raiz)
        
        if(vacioBol == True):
            self.pivote = True
            self.inserta = clave
            self.enlace = None
        else:
            pos = self.existeNodo(clave, raiz)
            
            if(self.existe == True):
                self.pivote = False
            else:
                self.agregar(clave, raiz.ramas[pos])
                
                if(self.pivote == True):
                    
                    if(raiz.cuentas < 4):
                        self.pivote = False;
                        self.insertarClave(self.inserta, raiz, pos)
                    else:
                        self.pivote = True
                        self.dividirPagina(self.inserta, raiz, pos)
                        print("Inserto")
            
            
    #Verificar si la raiz no Existe
    def vacio(self, raiz):
        if(raiz == None or raiz.cuentas == 0):
            return True
        else:
            return False
        
    
    #Insertar Claves en Pagina
    def insertarClave(self, clave, raiz, posicion):
        i = raiz.cuentas
        
        while i != posicion:
            raiz.claves[i] = raiz.claves[i - 1]
            raiz.ramas[i + 1] = raiz.ramas[i]
            i-=1
        
        raiz.claves[posicion] = clave
        raiz.ramas[posicion + 1] = self.enlace
        val = raiz.cuentas+1
        raiz.cuentas = val
        print("Inserto Valor")
        
        
    #Dividir Pagina
    def dividirPagina(self, clave, raiz, posicion):
        pos = 0
        Posmda = 0
        if(posicion <= 2):
            Posmda = 2
        else:
            Posmda = 3
        
        Mder = Pagina(ramas=[None,None,None,None,None], claves=[None,None,None,None], cuentas=0)
        pos = Posmda + 1
        
        while pos != 5:
            i = ((pos - Posmda) - 1)
            j = (pos - 1)
            Mder.claves[i] = raiz.claves[j]
            Mder.ramas[pos - Posmda] = raiz.ramas[pos]
            pos+=1
        
        Mder.cuentas = 4 - Posmda
        raiz.cuentas = Posmda
        
        if(posicion <= 2):
            self.insertarClave(clave, raiz, posicion)
        else:
            self.insertarClave(clave, Mder, (posicion - Posmda))
            
        self.inserta = raiz.claves[raiz.cuentas - 1]
        Mder.ramas[0] = raiz.ramas[raiz.cuentas]
        val = raiz.cuentas - 1
        raiz.cuentas = val
        self.enlace = Mder
        
    
    #Virificar si Existe el Nodo    
    def existeNodo(self, clave, raiz):
        valor =0
        if(clave.idNombre < raiz.claves[0].idNombre):
            self.existe2 = False
            valor = 0
        else:
            valor = raiz.cuentas
            while (clave.idNombre < raiz.claves[valor - 1].idNombre and valor > 1):
                valor-=1
            
            if (clave.idNombre < raiz.claves[valor - 1].idNombre):
                self.existe = True
            else:
                self.existe = False
            
            if (clave.idNombre == raiz.claves[valor - 1].idNombre):
                self.existe2 = True
            else:
                self.existe2 = False            
            
        
        return valor
    
    #Crear Archivo
    def dibujarArbol(self):
        archivo=open('arbolB.dot', 'w')
        archivo.write('digraph G{\n')
        archivo.write("node [shape = record];\n");
        archivo.write("rankdir = TD;\n");
        self.grabarArchivo(self.inicio , archivo)
        archivo.write('}')
        archivo.close() 
    def dibujar(self):
        return self.arbolcarpeta(self.inicio,"")  
    def buscarcarpeta(self,nombre):
        return self.buscararbolcarpeta(self.inicio,nombre) 
    def buscararbolcarpeta(self,raiz,cad):
        nodo = raiz
        
        if (nodo==None):
           
            return None
        else :
            if (nodo.cuentas != 0):
                
            
                k=1
                j=0
                while k <= nodo.cuentas:
                    if nodo.claves[k-1].nombreCarpeta == cad:
                        return nodo.claves[k-1].arbol
                    else:
                        b = nodo.claves[k-1].arbol.buscarcarpeta(cad)
                        if b!= None:
                            return b
                    if k== nodo.cuentas:
                        while str(self.buscararbolcarpeta(nodo.ramas[j],cad)) != "None":
                            b = self.buscararbolcarpeta(nodo.ramas[j],cad)
                            return b
                            j+=1        
                    k+=1

    def modificarcarpeta(self,nombre,nuevo):
        return self.modificararbolcarpeta(self.inicio,nombre,nuevo) 
    def modificararbolcarpeta(self,raiz,cad,nuevo):
        nodo = raiz
        
        if (nodo==None):
           
            return None
        else :
            if (nodo.cuentas != 0):
                
            
                k=1
                j=0
                while k <= nodo.cuentas:
                    if nodo.claves[k-1].nombreCarpeta == cad:
                        nodo.claves[k-1].nombreCarpeta = nuevo
                    else:
                        b = nodo.claves[k-1].arbol.modificarcarpeta(cad,nuevo)
                        if b!= None:
                            return b
                    if k== nodo.cuentas:
                        while str(self.modificararbolcarpeta(nodo.ramas[j],cad,nuevo)) != "None":
                            b = self.modificararbolcarpeta(nodo.ramas[j],cad,nuevo)
                            return b
                            j+=1        
                    k+=1                
    



    def eliminar(self,nombre,b):
        return self.eliminarcarpeta(self.inicio,nombre, b) 
    def eliminarcarpeta(self,raiz,cad,b):
        nodo = raiz
        
        if (nodo==None):
           
            return None
        else :
            if (nodo.cuentas != 0):
                
            
                k=1
                j=0
                while k <= nodo.cuentas:
                    if nodo.claves[k-1].nombreCarpeta == cad:
                        
                       hola="hola"
                    else:
                        b.InsertarArbolB(nodo.claves[k-1],self.inicio)
                        nodo.claves[k-1].arbol.eliminar(cad,b)
                        
                    if k== nodo.cuentas:
                        while str(self.eliminarcarpeta(nodo.ramas[j],cad,b)) != "None":
                            b = self.eliminarcarpeta(nodo.ramas[j],cad,b)
                            
                            j+=1        
                    k+=1
                return b          
    def buscaravl(self,nombre):
        return self.buscarnodoavl(self.inicio,nombre) 
    def buscarnodoavl(self,raiz,cad):
        nodo = raiz
        
        if (nodo==None):
           
            return None
        else :
            if (nodo.cuentas != 0):
                
            
                k=1
                j=0
                while k <= nodo.cuentas:
                    if nodo.claves[k-1].nombreCarpeta == cad:
                        return nodo.claves[k-1].nodoAVL
                    else:
                        b = nodo.claves[k-1].arbol.buscaravl(cad)
                        if b!= None:
                            return b
                    if k== nodo.cuentas:
                        while str(self.buscarnodoavl(nodo.ramas[j],cad)) != "None":
                            b = self.buscarnodoavl(nodo.ramas[j],cad)
                            return b
                            j+=1        
                    k+=1            
                
                """j=0
                while j <= nodo.cuentas:
                   
                    self.buscararbolcarpeta(nodo.ramas[j],cad)
                    j+=1"""

    def verarbol(self):
        return self.cadenaarbol(self.inicio,"")                
    
    def recorrerarbol(self, nombre):
        return self.crearcadena(self.inicio,"", nombre)                 
    
    def cadenaarbol(self,raiz,cad):
        nodo = raiz
        cadena=cad
        temp=""
        cont=0

        
        if (nodo==None):
           
            return None
        else :
            if (nodo.cuentas != 0):
                
            
                k=1
                
                j=0
                while k <= nodo.cuentas:
                    temp= str(nodo.claves[k-1].arbol.recorrerarbol(str(nodo.claves[k - 1].nombreCarpeta)))
                    if temp != "None":
                        cadena+= "root-" +str(nodo.claves[k-1].nombreCarpeta)+"@" + temp
                    else:
                        cadena+= "root-" +str(nodo.claves[k-1].nombreCarpeta)+"@"     
                     
                    if k== nodo.cuentas:
                        while str(self.cadenaarbol(nodo.ramas[j],"")) != "None":
                            cadena+= str(self.cadenaarbol(nodo.ramas[j],""))
                            j+=1

                    k+=1
                
                    
                print ("caden->"+cadena)
                return cadena    

                
               
                    

                             
    def crearcadena(self,raiz,cad,nombre):
        nodo = raiz
        cadena=cad
        if (nodo==None):
           
            return cadena
        else :
            if (nodo.cuentas != 0):
                #cadena+= nodo.claves[0].nombreCarpeta
                #print (cadena)
            
                k=1
                j=0
                while k <= nodo.cuentas:
                    temp = str(nodo.claves[k-1].arbol.recorrerarbol(nodo.claves[k-1].nombreCarpeta))
                    if temp != "None":
                        cadena+= nombre + "-"+ nodo.claves[k-1].nombreCarpeta + "@" + temp
                    else:
                        cadena+= nombre + "-"+ nodo.claves[k-1].nombreCarpeta + "@"   
                    if k== nodo.cuentas:
                        while str(self.crearcadena(nodo.ramas[j],"",nombre)) != "None":
                            cadena+= str(self.crearcadena(nodo.ramas[j],"",nombre))
                            j+=1

                    k+=1 
                return cadena        
                    

                   
    #Escribir Contenido del Archivo
    def grabarArchivo(self, raiz, archivo):
        nodo = raiz             
        if(nodo == None):
            print("No hay nada que Graficar")
        else:
            if (nodo.cuentas != 0):
                archivo.writelines("activo_" + str(nodo.claves[0].nombreCarpeta) + " [label= \"")
                k=1
                while k <= nodo.cuentas:
                    archivo.writelines("<r" + str(k - 1) + ">" + " | " + "<cl" + str(k) + ">"  + str(nodo.claves[k - 1].nombreCarpeta)+": SubCarpetas---:"+str(nodo.claves[k-1].arbol.dibujar()) + " &#92;" + " | ")
                    k+=1
                
                
                archivo.writelines("<r" + str(k - 1) + "> \"];\n")
                i=0
                while i <= nodo.cuentas:
                    if (nodo.ramas[i] != None):
                        if (nodo.ramas[i].cuentas != 0):
                            archivo.writelines("activo_" + str(nodo.claves[0].nombreCarpeta) + ":r" + str(i) + " -> activo_" + str(nodo.ramas[i].claves[0].nombreCarpeta) + ";\n")                          
                        
                    i+=1
                    
                j=0
                while j <= nodo.cuentas:
                    self.grabarArchivo(nodo.ramas[j],archivo)
                    j+=1
    def arbolcarpeta(self,raiz,cad):
        nodo = raiz
        cadena=cad
        if (nodo==None):
           
            return cadena
        else :
            if (nodo.cuentas != 0):
                #cadena+= nodo.claves[0].nombreCarpeta
                #print (cadena)
            
                k=1
                while k <= nodo.cuentas:
                    cadena+= "/" + nodo.claves[k-1].nombreCarpeta +": Sub:"+str(nodo.claves[k-1].arbol.dibujar())
                    #print (cadena)
                    k+=1
                i=0
                while i <= nodo.cuentas:
                    if (nodo.ramas[i] != None):
                        if (nodo.ramas[i].cuentas != 0):
                            cadena+="/"+nodo.claves[0].nombreCarpeta
                            #print (cadena)
                    i+=1
                j=0
                while j <= nodo.cuentas:
                   
                    self.arbolcarpeta(nodo.ramas[j],cadena)
                    j+=1
                if j > nodo.cuentas:
                    return cadena    
                        


                        
class Nodo:
    def __init__(self,usuario, contra):
        self.usuario=usuario
        self.contra=contra
        self.raizB = ArbolB()
        self.siguiente= None
        self.anterior = None
class ListaCircularDobleEnlazada:
    def __init__(self):
        self.primero=None
        self.ultimo=None
    def vacia(self):
        if self.primero == None:
            return True
        else:
            return False
    def agregar_inicio(self, usuario,contra):
        if self.vacia() :
            self.primero= self.ultimo = Nodo(usuario,contra)
        else:
            aux = Nodo(usuario,contra)
            aux.siguiente = self.primero
            self.primero.anterior = aux;
            self.primero = aux
        self.__unir_nodos()
    def agregar_final(self,usuario, contra):
        if self.vacia():
            self.primero = self.ultimo = Nodo(usuario,contra)
        else:
            aux = self.ultimo
            self.ultimo = aux.siguiente = Nodo(usuario,contra)
            self.ultimo.anterior = aux
        self.__unir_nodos()
    def __unir_nodos(self):
        self.primero.anterior = self.ultimo
        self.ultimo.siguiente = self.primero
    def recorrer_inicio_fin(self):
        aux= self.primero
        while aux:
            print(str(aux.usuario) + "--" + str(aux.contra) )
            aux =aux.siguiente
            if aux == self.primero:
                break
    def recorrer_fin_inicio(self):
        aux=self.ultimo
        while aux:
            print (str(aux.usuario) + "--" + str(aux.contra))
            aux = aux.anterior
            if aux ==  self.ultimo:
                break
    def buscar(self, user, passw):
        aux= self.primero
        check= False
       
        while aux:
            if aux.usuario == user and aux.contra == passw:
                check=True
                break
            aux = aux.siguiente
            if aux == self.primero:
                break
        if check ==  True:
            return "Si"
        else:
            return "No"
    def obtenerArbolB(self, nombre = None):
        aux = self.primero
        while aux != None:
            if aux.usuario == nombre:
                return aux.raizB           
            aux = aux.siguiente  
            if  aux == self.primero:
                return "false"
    def cambiarArbolb(self, nombre = None,b=None):
        aux = self.primero
        while aux != None:
            if aux.usuario == nombre:
                aux.raizB = b          
            aux = aux.siguiente  
            if  aux == self.primero:
                return "false"            





from flask import Flask, request


app = Flask(__name__)

ListaUsuarios = ListaCircularDobleEnlazada()

@app.route('/Hola')
def hello_world():
    return 'Hello from Flask sale!'
@app.route('/Vamos')
def  h10():
    return 'dale!'

@app.route('/post',methods=['POST'])
def h():
    parametroPython = request.form['parametro']
    return "201212644 " + parametroPython
@app.route('/Usuario', methods=['POST'])
def h1():
    
    usuario = request.form['usuario']
    contra = request.form['contra']
    ListaUsuarios.agregar_final(usuario,contra)
    return "True esta hecho"
@app.route('/Check', methods = ['POST'])
def h2():
    usuario = request.form['usuario']
    contra = request.form['contra']
    verificar =ListaUsuarios.buscar(usuario,contra)
    print (verificar)
    return verificar
@app.route('/ModificarCarpeta', methods = ['POST'])
def h22(): 
    usuario = request.form['usuario']
    nombre = request.form['nombre']
    nuevo = request.form['nuevo']
    raizB = ListaUsuarios.obtenerArbolB(usuario)
    raizB.modificarcarpeta(nombre,nuevo)
    raizB.dibujarArbol()

    return "True"
@app.route('/EliminarCarpeta', methods = ['POST'])
def h23():
     a = ArbolB()   
     usuario = request.form['usuario']
     carpeta= request.form['carpeta']
     raizB = ListaUsuarios.obtenerArbolB(usuario)
     b= raizB.eliminar(carpeta,a)

     ListaUsuarios.cambiarArbolb(usuario,b)
     raizB.dibujarArbol()

     return "True"




@app.route('/CrearCarpeta', methods = ['POST'])
def h3():
    
    a= ArbolB()
    c = AVLTree()
    usuario = request.form['usuario']
    nombre = request.form['nombre']
    padre = request.form['padre']
    raizB = ListaUsuarios.obtenerArbolB(usuario)

    b = raizB.buscarcarpeta(padre)

    abd="abcdefghijklmnopqrstuvwxyz"
    primera=nombre[0]
    i=0
    for letra in abd:
        if letra==primera:
            break
        else:
            i=i+1
    if b == None:
        raizB.crearNodoInsertar(i,nombre,"c1",a)
    else:
        b.crearNodoInsertar(i,nombre,"c1",a)           

  
   
    
    cadena =""
    raizB.dibujarArbol()
    cadena += raizB.verarbol()
    
    return cadena
@app.route('/CrearArchivo', methods = ['POST'])
def h6():
    usuario= request.form['usuario']
    nombrecarpeta= request.form['carpeta']
    nombrearchivo = request.form['nombre']
    archivo = request.form['archivo']
    raizB = ListaUsuarios.obtenerArbolB(usuario)

    b = raizB.buscaravl(nombrecarpeta)
   
        
    abd="abcdefghijklmnopqrstuvwxyz"
    primera=nombrearchivo[0]
    i=0
    if primera.isdigit() == True:
        i= primera
    else:    
        for letra in abd:
            if letra==primera:
                break
            else:
                i=i+1

    if b==None:
        raizB.avlre().insert(nombrearchivo,nombrearchivo,archivo)
       
     

        raizB.avlre().dibujarAvl()
    else:
        b.insert(nombrearchivo,nombrearchivo,archivo) 
       
        b.dibujarAvl()  
    

    return "Listo"


if __name__ == "__main__":
 """b = AVLTree()
 b.insert(0,"hola","archivo")
 b.insert(0,"jus","archivo")
 b.dibujarAvl()"""
 ListaUsuarios.agregar_final("jose","1")    
 app.run(debug=True, host='192.168.1.7')
