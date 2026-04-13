### **Questão 1: Colisão no CSMA**

> _Suponha que dois nós comecem a transmitir ao mesmo tempo um pacote de comprimento L por um canal broadcast de velocidade R. Denote o atraso de propagação entre os dois nós como $d_{prop}$. Haverá uma colisão se $d_{prop} < L/R$? Por quê?_

**Sim, haverá uma colisão.**

Se dois nós em um canal de difusão começam a transmitir **exatamente ao mesmo tempo**, seus sinais obrigatoriamente se cruzarão no meio do caminho e sofrerão interferência (colisão), independentemente da relação entre $d_{prop}$ (tempo que o sinal leva para viajar no cabo) e $L/R$ (tempo necessário para colocar todos os bits do pacote no cabo). Como ambos iniciaram no tempo $t=0$, o primeiro bit do nó A e o primeiro bit do nó B se encontrarão no meio do canal no tempo $d_{prop}/2$. Como o tempo de transmissão é maior que o tempo de propagação ($L/R > d_{prop}$), quando a colisão ocorrer, os nós ainda estarão ativamente transmitindo o restante de seus pacotes, resultando em dados corrompidos.

---

### **Questão 2: Backoff Exponencial no CSMA/CD**

> _No CSMA/CD, depois da quinta colisão, qual é a probabilidade de um nó escolher K = 4?_

No protocolo Ethernet CSMA/CD, quando ocorre uma colisão, o nó usa o algoritmo de **Recuo Exponencial Binário** (Binary Exponential Backoff) para esperar um tempo aleatório antes de tentar novamente.

A regra é: após a $n$-ésima colisão consecutiva, o nó escolhe um valor aleatório de $K$ a partir de um conjunto $\{0, 1, 2, ..., 2^n - 1\}$.

Neste caso, ocorreu a 5ª colisão ($n=5$).

- O conjunto de escolha é $\{0, 1, 2, ..., 2^5 - 1\}$, ou seja, de $0$ a $31$.
    
- Existem 32 valores possíveis e todos têm a mesma probabilidade de serem escolhidos.
    
- Portanto, a probabilidade de escolher especificamente o número $4$ é **1/32** (aproximadamente **3,125%**).
    

---

### **Questão 3: Espaços de Endereçamento**

> _Que tamanho tem o espaço de endereços MAC? E o espaço de endereços IPv4? E o espaço de endereços IPv6?_

Esses são os tamanhos fixos padronizados na arquitetura de redes:

- **Endereço MAC:** Possui 48 bits de comprimento. O espaço total é de $2^{48}$ endereços (mais de 281 trilhões).
    
- **Endereço IPv4:** Possui 32 bits de comprimento. O espaço total é de $2^{32}$ endereços (cerca de 4,3 bilhões).
    
- **Endereço IPv6:** Possui 128 bits de comprimento. O espaço total é de $2^{128}$ endereços (um número astronomicamente grande, suficiente para endereçar cada grão de areia da Terra).
    

---

### **Questão 4: Processamento de Quadros (Sniffing)**

> _Suponha que cada um dos nós A, B e C esteja ligado à mesma LAN de difusão. Se A enviar milhares de datagramas IP a B com quadro de encapsulamento endereçado ao endereço MAC de B, o adaptador de C processará esses quadros? Se processar, ele passará os datagramas IP desses quadros para C? O que mudaria em suas respostas se A enviasse quadros com o endereço MAC de difusão?_

- **Caso 1 (Destino MAC de B):** Como é uma LAN de difusão (como um hub ou barramento antigo), o sinal elétrico chega ao adaptador de rede de C. O adaptador de C processará apenas o cabeçalho do quadro e verificará o endereço MAC de destino. Como o MAC não é o de C, **o adaptador descartará o quadro imediatamente**. Ele **não** passará o datagrama IP para a camada de rede do nó C.
    
- **Caso 2 (Destino MAC de Difusão):** Se A enviar com o endereço MAC de difusão (`FF:FF:FF:FF:FF:FF`), o adaptador de C receberá o quadro, verá que é um broadcast (destinado a todos) e o aceitará. Ele removerá o cabeçalho Ethernet e **passará o datagrama IP para a camada superior** (camada de rede) do nó C processar.
    

---

### **Questão 5: O Protocolo ARP**

> _Por que uma pesquisa ARP é enviada dentro de um quadro de difusão? Por que uma resposta ARP é enviada em um quadro com um endereço MAC de destino específico?_

- **Pesquisa ARP (Difusão):** Quando um nó conhece o IP de destino, mas não sabe o endereço MAC físico correspondente, ele precisa perguntar à rede inteira: "Qual adaptador tem este endereço IP?". Como ele não sabe quem deve responder, a única forma de garantir que o dono do IP ouça a pergunta é gritando para todos na sub-rede (difusão MAC `FF:FF:FF:FF:FF:FF`).
    
- **Resposta ARP (Unicast/Específico):** O nó alvo, ao ouvir a pergunta, constrói a resposta. Ele não precisa usar difusão para responder, pois o quadro da pesquisa ARP já continha o endereço MAC do nó remetente original. Enviar diretamente (unicast) economiza largura de banda e evita interromper as placas de rede dos outros dispositivos da rede que não estão participando dessa conversa.
    

---

### **Questões 6 e 7: Roteamento Inter-LANs e MAC/IP**

> _(Resumo: Como um pacote viaja de A para F passando por dois roteadores e três sub-redes)._

Vou mapear a topologia clássica para resolver as duas questões juntas:

- **Sub-rede 1 (192.168.1.xxx):** Hospedeiro A (`192.168.1.10`, MAC-A) e Interface 1 do Roteador E (`192.168.1.1`, MAC-RE1).
    
- **Sub-rede 2 (192.168.2.xxx):** Interface 2 do Roteador E (`192.168.2.1`, MAC-RE2) e Interface 1 do Roteador D (`192.168.2.2`, MAC-RD1).
    
- **Sub-rede 3 (192.168.3.xxx):** Interface 2 do Roteador D (`192.168.3.1`, MAC-RD2) e Hospedeiro F (`192.168.3.10`, MAC-F).
    

**A viagem de A para F (Regra de Ouro: Endereços IP de origem/destino NUNCA mudam; Endereços MAC mudam a cada salto):**

**Etapa (i): De A até o Roteador Esquerdo (RE)**

A percebe que F está em outra rede. Ele precisa enviar para seu gateway padrão (RE).

- **IP Origem:** 192.168.1.10 (A) | **IP Destino:** 192.168.3.10 (F)
    
- **MAC Origem:** MAC-A | **MAC Destino:** MAC-RE1
    

**Etapa (ii): Do Roteador Esquerdo (RE) ao Roteador Direito (RD)**

RE consulta sua tabela de repasse e vê que para chegar na Sub-rede 3, deve enviar para RD através da Sub-rede 2.

- **IP Origem:** 192.168.1.10 (A) | **IP Destino:** 192.168.3.10 (F)
    
- **MAC Origem:** MAC-RE2 | **MAC Destino:** MAC-RD1
    

**Etapa (iii): Do Roteador Direito (RD) até F**

RD consulta a tabela e vê que F está em sua rede diretamente conectada.

- **IP Origem:** 192.168.1.10 (A) | **IP Destino:** 192.168.3.10 (F)
    
- **MAC Origem:** MAC-RD2 | **MAC Destino:** MAC-F
    

---

### **Questões 8, 9 e 10: Vazão (Throughput) com Switches vs Hubs**

> _(Topologia inferida: 9 hospedeiros e 2 servidores interconectados. Enlaces de 100 Mbps)._

**Questão 8 (Tudo Switch):**

- Comutadores (switches) isolam domínios de colisão, permitindo transmissões paralelas (não interferem entre si).
    
- Temos 11 nós no total. Para maximizar a vazão, podemos formar **5 pares** de nós conversando simultaneamente (sobra 1 nó ocioso).
    
- Se cada enlace tem **100 Mbps** e a matriz do switch for não-bloqueante, temos 5 conversas ocorrendo a 100 Mbps cada.
    
- **Vazão Máxima Agregada:** $5 \times 100 = 500 \text{ Mbps}$. _(Nota: Se assumirmos enlaces full-duplex, onde todos enviam e recebem ao mesmo tempo em vias separadas, teoricamente chegaria a 1.1 Gbps, mas 500 Mbps é a resposta esperada padrão para fluxos half-duplex pareados nestes exercícios)._
    

**Questão 9 (Switches Departamentais Substituídos por Hubs):**

- Hubs são repetidores burros; eles criam um único domínio de colisão por departamento. Apenas um nó por departamento pode transmitir por vez sem causar colisão.
    
- A vazão agregada cai severamente porque a concorrência pelo meio compartilhado dentro de cada departamento dita o ritmo, limitando a comunicação paralela. O limite de tráfego que sai de cada hub para o switch central é de apenas 100 Mbps divididos por todos no hub.
    

**Questão 10 (Todos os Equipamentos são Hubs):**

- Agora, a rede inteira é um gigantesco e único domínio de colisão.
    
- Apenas um nó na rede inteira pode transmitir com sucesso em um dado momento.
    
- **Vazão Máxima Agregada:** **100 Mbps**. (Na prática será muito menor devido às constantes colisões, mas o limite físico absoluto agregado do canal compartilhado é 100 Mbps).
    

---

### **Questão 11: Aprendizado Transparente do Switch (Tabela de MACs)**

> _(Comutador estrela com nós A, B, C, D, E, F. Tabela inicial vazia)._

Switches preenchem sua tabela (MAC -> Porta) observando o endereço de **origem** de cada quadro que chega.

**Estado Inicial da Tabela:** [ Vazia ]

**Evento (i): B envia um quadro a E.**

- **Aprendizado:** Switch lê a origem (B) na porta associada e registra: `[B -> Porta do B]`.
    
- **Encaminhamento:** O switch olha o destino (E). Como 'E' não está na tabela, o switch faz o que chamamos de **inundação (flood)**. Ele encaminha o quadro para **todos os enlaces**, exceto aquele por onde o quadro chegou (Portas A, C, D, E, F).
    
- **Tabela após (i):** `[B: Porta B]`
    

**Evento (ii): E responde com um quadro a B.**

- **Aprendizado:** Switch lê a origem (E) e registra: `[E -> Porta do E]`.
    
- **Encaminhamento:** O switch olha o destino (B). Ele procura 'B' na tabela e encontra! O switch encaminha o quadro **apenas para o enlace do nó B** (unicast).
    
- **Tabela após (ii):** `[B: Porta B]`, `[E: Porta E]`
    

**Evento (iii): A envia um quadro a B.**

- **Aprendizado:** Switch lê a origem (A) e registra: `[A -> Porta do A]`.
    
- **Encaminhamento:** O switch olha o destino (B). 'B' já está na tabela. Ele encaminha o quadro **apenas para o enlace do nó B**.
    
- **Tabela após (iii):** `[B: Porta B]`, `[E: Porta E]`, `[A: Porta A]`
    

**Evento (iv): B responde com um quadro a A.**

- **Aprendizado:** Switch lê a origem (B). Já existe, mas ele zera o temporizador de expiração (Time to Live) da entrada `[B -> Porta B]`.
    
- **Encaminhamento:** O switch olha o destino (A). 'A' está na tabela. Ele encaminha o quadro **apenas para o enlace do nó A**.
    
- **Tabela após (iv):** `[B: Porta B]`, `[E: Porta E]`, `[A: Porta A]`