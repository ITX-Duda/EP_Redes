### **Questão 1: Roteamento vs. Repasse**

> _Qual é a diferença entre roteamento e repasse?_

Esta é uma distinção clássica e importantíssima na camada de rede.

- **Repasse (Forwarding):** É uma ação **local** que ocorre dentro de um único roteador. Quando um pacote chega a uma porta de entrada, o roteador examina o cabeçalho do pacote e o transfere (repassa) para a porta de saída correta. Pense nisso como um guarda de trânsito em um cruzamento dizendo "vire à direita" baseado na placa do carro. Operacionalmente, isso acontece em hardware (plano de dados) para ser muito rápido.
    
- **Roteamento (Routing):** É uma ação **global** que envolve a rede inteira. Consiste nos algoritmos (como OSPF ou BGP) que calculam os melhores caminhos (rotas) que os pacotes devem seguir desde o hospedeiro de origem até o de destino. Pense nisso como o aplicativo de GPS mapeando toda a sua viagem. O roteamento (plano de controle) é o que constrói as tabelas que o repasse utilizará.
    

---

### **Questão 2: Tabelas de Repasse**

> _Os roteadores nas redes de datagramas e nas redes de circuitos virtuais usam tabelas de repasse?_ _Caso usem, descreva as tabelas de repasse para ambas as classes de redes._

**Sim, ambos usam!** O repasse de pacotes não existe sem uma tabela de consulta.

- **Redes de Datagramas (ex: Internet IP):** Os pacotes não têm uma conexão estabelecida. A tabela de repasse mapeia **faixas de endereços IP de destino** para portas de saída específicas. Quando um pacote chega, o roteador extrai o IP de destino e procura na tabela a qual intervalo ele pertence.
    
- **Redes de Circuitos Virtuais (VC):** Uma conexão lógica (circuito) é criada antes da transmissão. Os pacotes recebem um número de VC. A tabela de repasse mapeia a combinação de **(Porta de Entrada, Número VC de Entrada)** para **(Porta de Saída, Número VC de Saída)**. O roteador altera o número do VC no cabeçalho antes de enviar o pacote adiante.
    

---

### **Questão 3: Cópia de Sombra da Tabela**

> _Discuta por que cada porta de entrada em um roteador de alta velocidade armazena uma cópia de sombra da tabela de repasse._

Em roteadores modernos de alta velocidade, processar tudo na unidade central de processamento (CPU) criaria um tremendo gargalo. Ao armazenar uma cópia da tabela de repasse (a "cópia de sombra") diretamente no hardware de cada porta de entrada, o roteador pode realizar buscas descentralizadas em paralelo. Assim, cada porta toma decisões de repasse de forma independente e instantânea ("velocidade de linha"), sem precisar interromper o processador central a cada pacote.

---

### **Questão 4: Perda de Pacotes na Entrada**

> _Descreva como pode ocorrer perda de pacotes em portas de entrada._ _Descreva como a perda de pacotes pode ser eliminada em portas de entrada (sem usar buffers infinitos)._

- **Como ocorre:** A perda acontece se os pacotes chegarem à porta de entrada mais rápido do que o "tecido de comutação" (switching fabric) do roteador consegue processá-los e movê-los para as portas de saída. As filas na porta de entrada enchem, e os novos pacotes são descartados. Isso também acontece devido ao _Head-of-Line (HOL) blocking_, onde um pacote na frente da fila que precisa ir para uma porta de saída congestionada bloqueia os pacotes atrás dele que vão para portas livres.
    
- **Como eliminar:** A teoria de redes nos diz que se a velocidade do tecido de comutação for pelo menos $n$ vezes mais rápida que a velocidade da linha de entrada (onde $n$ é o número total de portas de entrada), não haverá formação de fila na entrada, eliminando o problema estrutural de perda ali (assumindo que não há gargalos na saída).
    

---

### **Questão 5: Perda de Pacotes na Saída**

> _Descreva como pode ocorrer perda de pacotes em portas de saída._ _Essa perda poderia ser impedida aumentando a velocidade de fábrica do comutador?_

- **Como ocorre:** Acontece quando vários pacotes vêm de diferentes portas de entrada para uma mesma porta de saída simultaneamente. Se a taxa combinada desses pacotes for maior que a capacidade de transmissão do link de saída, o buffer da porta de saída começa a encher. Quando enche completamente, pacotes são perdidos.
    
- **Aumentar a velocidade da fábrica ajuda? Não.** Na verdade, aumentar a velocidade do tecido de comutação faria com que os pacotes fossem empurrados para a porta de saída ainda mais rápido, agravando o congestionamento no buffer de saída. O limite aqui é a velocidade física do link de saída, não a arquitetura interna.
    

---

### **Questão 6: Roteadores e Endereços IP**

> Roteadores têm endereços IP? Em caso positivo, quantos?

**Sim.** Roteadores operam na camada de rede, portanto precisam de endereços IP. Um roteador possui **um endereço IP diferente para cada interface de rede** (porta) que ele tem conectada. Por exemplo, se ele conecta 4 sub-redes diferentes, ele terá 4 portas ativas e 4 endereços IP distintos (um pertencente a cada sub-rede).

---

### **Questão 7: Transitando pelas Interfaces**

> _Suponha que haja três roteadores entre os hospedeiros de origem e de destino._ _Ignorando a fragmentação, um datagrama IP enviado do hospedeiro de origem até o hospedeiro de destino transitará por quantas interfaces?_ _Quantas tabelas de repasse serão indexadas para deslocar o datagrama desde a origem até o destino?_

Vamos desenhar o fluxo mentalmente:

`Origem -> Roteador 1 -> Roteador 2 -> Roteador 3 -> Destino`

- **Interfaces:**
    
    1. Sai pela interface da Origem.
        
    2. Entra na interface do Roteador 1 e sai pela outra interface do Roteador 1 (+2).
        
    3. Entra na interface do Roteador 2 e sai pela outra (+2).
        
    4. Entra na interface do Roteador 3 e sai pela outra (+2).
        
    5. Entra na interface do Destino (+1).
        
        **Total:** 8 interfaces transitadas (1 origem + 6 nos roteadores + 1 no destino).
        
- **Tabelas de Repasse Indexadas:** Apenas roteadores fazem repasse de fato. Como há 3 roteadores, o datagrama precisará de consulta em **3 tabelas de repasse**.
    

---

### **Questão 8: DHCP e NAT em Redes Domésticas**

> _Suponha que você compre um roteador sem fio e o conecte a seu modem a cabo._ _Suponha também que seu ISP designe dinamicamente um endereço IP a seu dispositivo conectado (isto é, seu roteador sem fio)._ _Suponha ainda que você tenha cinco PCs em casa e que usa 802.11 para conectá-los sem fio ao roteador._ Como são designados endereços IP aos cinco PCs? O roteador sem fio usa NAT? Por quê?

- **Designação aos PCs:** O seu roteador sem fio possui um servidor **DHCP** (Dynamic Host Configuration Protocol) embutido. Ele distribui endereços IP "privados" ou "locais" (como 192.168.1.x) automaticamente para os cinco PCs quando eles se conectam ao Wi-Fi.
    
- **Usa NAT?** **Sim.** O roteador sem fio usa NAT (Network Address Translation).
    
- **Por quê?** Porque o seu Provedor de Internet (ISP) forneceu apenas um único endereço IP público para a sua casa (designado dinamicamente para o roteador ). Os 5 PCs têm IPs privados que não são roteáveis na internet aberta. O NAT traduz o endereço privado + porta de origem dos PCs para o único endereço IP público do roteador, permitindo que toda a sua casa compartilhe uma única conexão com o mundo exterior.
    

---

### **Questão 9: Protocolos Inter-AS e Intra-AS**

> _Por que são usados protocolos inter-AS e intra-AS diferentes na Internet?_

"AS" significa Sistema Autônomo (Autonomous System), que é a rede de uma organização ou provedor.

- **Escala:** Protocolos Inter-AS (como o BGP) precisam lidar com a tabela global de roteamento da Internet (centenas de milhares de rotas). Protocolos Intra-AS (como OSPF ou IS-IS) gerenciam rotas apenas dentro de uma rede local (algumas dezenas ou centenas de roteadores).
    
- **Políticas vs. Desempenho:**
    
    - **Inter-AS (BGP):** A principal preocupação são as **políticas**. Um provedor pode querer ditar: "O tráfego do provedor A não pode cruzar minha rede para chegar ao provedor B porque eles não me pagam". O BGP permite roteamento baseado nessas complexas regras de negócios.
        
    - **Intra-AS (OSPF):** A principal preocupação é o **desempenho**. Dentro de sua própria rede, a organização quer apenas que o pacote chegue da forma mais rápida e barata possível.
        

---

### **Questão 10: Roteamento baseado em Datagrama (Rede da Figura 1)**

> _Considere a rede da figura abaixo._ a. Suponha que seja uma rede de datagramas. Mostre a tabela de repasse no roteador A, de modo que todo o tráfego destinado ao hospedeiro H3 seja encaminhado pela interface 3.

Em redes de datagramas, a tabela de repasse clássica examina apenas o **Endereço de Destino**.

- **Tabela do Roteador A:**
    
    |**Destino**|**Interface de Saída**|
    |---|---|
    |H3|3|
    

> b. Suponha que esta rede seja uma rede de datagramas. _Você consegue compor uma tabela de repasse no roteador A, de modo que todo o tráfego de H1 destinado ao hospedeiro H3 seja encaminhado pela interface 3, enquanto todo o tráfego de H2 [destinado a H3 vá por outro lugar?]_

**Não.** Em uma rede de datagramas padrão estrita do capítulo 4 do livro-texto (arquitetura IP clássica), o roteador só toma decisões baseadas no **IP de destino**. A tabela de repasse não guarda estado sobre a origem do pacote (H1 ou H2). Para fazer o que a questão pede, seria necessário roteamento baseado em políticas (PBR) ou SDN (Software-Defined Networking), que não é o comportamento padrão de uma tabela de repasse IP simples baseada apenas em destino.

---

### **Questão 11: Elementos de Comutação**

> _Suponha que dois pacotes cheguem a duas portas de entrada diferentes de um roteador exatamente ao mesmo tempo._ _Suponha também que não haja outros pacotes em lugar algum no roteador._

> a. Suponha que os dois pacotes devam ser repassados a duas portas de saída diferentes. _É possível repassar os dois pacotes pelo elemento de comutação ao mesmo tempo quando o elemento usa um barramento compartilhado?_

**Não.** Um barramento (bus) só permite a passagem de um único pacote por vez. Mesmo que vão para portas de saída diferentes, eles precisam cruzar o mesmo barramento físico.

> b. Imagine que os dois pacotes devam ser repassados a duas portas de saída diferentes. _É possível repassar os dois pacotes pelo elemento de comutação ao mesmo tempo quando o elemento usa o tipo crossbar?_

**Sim.** Arquiteturas crossbar (matriz de interconexão) permitem múltiplas travessias simultâneas, contanto que as portas de origem e os destinos sejam diferentes e os caminhos na malha não se cruzem num mesmo ponto de junção bloqueado.

> c. Considere que os dois pacotes devam ser repassados para a mesma porta de saída. _É possível repassar os dois pacotes pelo elemento de comutação ao mesmo tempo quando o elemento usa um tipo crossbar?_

**Não.** A limitação agora não é o tecido crossbar, mas sim a placa da porta de saída. Apenas um pacote pode acessar a fila/buffer de uma única interface de saída por vez.

---

### **Questão 12: Sub-redes e Endereçamento IP**

> Considere um roteador que interconecta três sub-redes: 1, 2 e 3. Suponha que todas as interfaces de cada uma dessas três sub-redes tenha de ter o prefixo 223.1.17/24. Suponha também que a sub-rede 1 tenha de suportar até 60 interfaces, a sub-rede 2 tenha de suportar até 90 interfaces e a sub-rede 3, 12 interfaces. _Dê três endereços de rede (da forma a.b.c.d/x) que satisfaçam essas limitações._

O bloco mestre é `223.1.17.0/24` (256 endereços: 0 a 255). Sempre começamos alocando a rede maior.

1. **Sub-rede 2 (90 interfaces):** Precisamos da potência de 2 superior, que é 128 (máscara /25).
    
    - Endereço: **223.1.17.0/25** (Usa de 0 a 127).
        
2. **Sub-rede 1 (60 interfaces):** A potência de 2 superior é 64 (máscara /26). Começamos de onde a anterior parou.
    
    - Endereço: **223.1.17.128/26** (Usa de 128 a 191).
        
3. **Sub-rede 3 (12 interfaces):** A potência de 2 superior é 16 (máscara /28). Começamos de onde a anterior parou.
    
    - Endereço: **223.1.17.192/28** (Usa de 192 a 207).
        

---

### **Questão 13: Divisão de Blocos IP**

> Considere uma sub-rede com prefixo 128.119.40.128/26. Dê um exemplo de um endereço IP (na forma xxx.xxx.xxx.xxx) que possa ser designado para essa rede.

Uma máscara /26 pega os últimos 6 bits do IP para os hosts. Isso dá 64 endereços no bloco (do .128 ao .191). O IP `.128` é a rede e `.191` é o broadcast. Portanto, qualquer IP entre **128.119.40.129** e **128.119.40.190** é um exemplo válido.

> _Suponha que um ISP possua o bloco de endereços na forma 128.119.40.64/26._ _Suponha que ele queira criar quatro sub-redes a partir desse bloco, e que cada bloco tenha o mesmo número de endereços IP._ _Quais são os prefixos (na forma a.b.c.d/x) para as quatro sub-redes?_

O bloco /26 possui 64 endereços. Queremos dividi-lo em 4 blocos.

$64 \div 4 = 16$ endereços por bloco.

Blocos de 16 endereços usam uma máscara `/28` ($32 - 4 \text{ bits para host} = 28$). Para encontrar os prefixos, basta somar 16 ao último octeto a partir do endereço base:

- Sub-rede 1: **128.119.40.64/28**
    
- Sub-rede 2: **128.119.40.80/28**
    
- Sub-rede 3: **128.119.40.96/28**
    
- Sub-rede 4: **128.119.40.112/28**
    

---

### **Questão 14: Fragmentação IPv4 (Tamanho MTU 700)**

> _Considere enviar um datagrama de 2.400 bytes por um enlace que tem uma MTU de 700 bytes._ _Suponha que o datagrama original esteja marcado com o número de identificação 422. Quantos fragmentos são gerados?_ _Quais são os valores em vários campos dos datagramas IP gerados em relação à fragmentação?_

O datagrama original tem 2400 bytes totais. Assumindo um cabeçalho IP padrão de 20 bytes, há 2380 bytes de _payload_ (dados).

A MTU (Maximum Transmission Unit) do enlace é 700 bytes. O tamanho de dados útil por fragmento é: $700 - 20 (\text{cabeçalho}) = 680 \text{ bytes}$. (Note que 680 é múltiplo de 8, requisito da fragmentação IPv4).

Quantidade de fragmentos gerados: $2380 \div 680 = 3.5$. Precisaremos de **4 fragmentos**.

Valores dos campos:

- **Fragmento 1:** Dados = 680 bytes. Cabeçalho = 20. (Total = 700). ID = 422. Flag (Mais Fragmentos) = 1. Deslocamento = 0.
    
- **Fragmento 2:** Dados = 680 bytes. Cabeçalho = 20. (Total = 700). ID = 422. Flag = 1. Deslocamento = 85 ($680 \div 8$).
    
- **Fragmento 3:** Dados = 680 bytes. Cabeçalho = 20. (Total = 700). ID = 422. Flag = 1. Deslocamento = 170 ($1360 \div 8$).
    
- **Fragmento 4:** Dados restantes = 340 bytes ($2380 - (680 \times 3)$). Cabeçalho = 20. (Total = 360 bytes). ID = 422. Flag = 0. Deslocamento = 255 ($2040 \div 8$).
    

---

### **Questão 15: Fragmentação (Arquivo MP3)**

> _Suponha que entre o hospedeiro de origem A e o hospedeiro destinatário B os datagramas estejam limitados a 1.500 bytes (incluindo cabeçalho)._ _Admitindo um cabeçalho IP de 20 bytes, quantos datagramas seriam necessários para enviar um arquivo MP3 de 5 milhões de bytes?_ _Explique como você obteve a resposta._

1. **Limitar a carga útil:** Se a MTU é 1500 bytes e o cabeçalho gasta 20 bytes, cada pacote pode carregar **1480 bytes** de dados reais do MP3.
    
2. **Calcular fragmentos:** Você divide o tamanho do arquivo pelo tamanho de payload de cada pacote: $5.000.000 \div 1480 \approx 3378.37$.
    
3. **Resposta:** Como você não pode enviar um "quarto" de datagrama, sempre se arredonda para cima. Portanto, serão necessários **3379 datagramas**.
    

---

### **Questão 16: Algoritmo de Dijkstra**

> Considere a rede da figura abaixo. Com os custos de enlace indicados, use o algoritmo do caminho mais curto de Dijkstra para calcular o caminho mais curto de x até todos os nós da rede. _Mostre como o algoritmo funciona calculando uma tabela semelhante à mostrada em aula._

O Algoritmo de Dijkstra (também chamado de Algoritmo de Estado de Enlace ou _Link-State_) constrói uma árvore de caminhos mais curtos passo a passo. Ele mantém os custos conhecidos `D(v)` para todos os destinos a partir do nó fonte (aqui, o nó **x**). Como não tenho acesso total à topologia exata desenhada sem uma digitalização perfeita (apenas os pesos avulsos ), aqui está a mecânica fundamental (tabela) que você precisaria estruturar para a sua resposta formal baseada no grafo que você tem na sua tela física:

_Passo a passo lógico para a tabela:_

1. **Passo 0 (Inicialização):** Você define o nó fonte (`x`). Observa os vizinhos diretos de `x` e anota o custo até eles. Nós não diretamente conectados recebem valor de custo infinito ($\infty$).
    
2. **Passo 1:** Analise todos os nós ainda não definitivos (todos menos `x`). Escolha o nó com o menor custo `D(v)`. Adicione-o à lista de nós visitados/definitivos.
    
3. **Passo 2:** Atualize os custos para os vizinhos adjacentes do nó recém-escolhido. A fórmula de atualização compara o custo antigo com o novo caminho proposto:
    
    $$D(v) = \min \{ D(v), D(u) + c(u,v) \}$$
    
    (Onde $u$ é o nó recém-incorporado e $c(u,v)$ é o custo do link entre eles).
    
4. Repita até que todos os nós da rede estejam no conjunto definitivo. A cada iteração, você estará registrando também o predecessor lógico na tabela para poder traçar a rota de volta depois.
    

---

### **Questão 17: Vetor de Distâncias (Nó Z)**

> Considere a rede da figura abaixo. Admita que cada nó inicialmente conheça os custos até cada um de seus vizinhos. _Considere o algoritmo de vetor de distâncias e mostre os registros na tabela de distâncias para o nó z._

No algoritmo do Vetor de Distâncias (Distance Vector), utilizando a Equação de Bellman-Ford, cada nó mantém uma tabela onde as linhas representam seus vizinhos imediatos e as colunas representam todos os destinos na rede.

Para o nó **z**, os vizinhos imediatos observados são **v** (custo = 6 ) e **x** (custo = 2 ).

A lógica da equação central é:

$$D_z(Y) = \min_w \{ c(z,w) + D_w(Y) \}$$

Sendo $w$ os vizinhos ($v$ e $x$).

O nó `z` primeiramente anota o que sabe: ir para `v` custa 6 diretamente. Ir para `x` custa 2 diretamente.

A tabela inicial de `z` só tem informações completas (não-infinitas) para seus vizinhos. Logo após, `v` e `x` enviarão seus vetores para `z`. `z` usará a fórmula acima para calcular, por exemplo, o custo para chegar em `y` e `u`, comparando "Quanto custa ir via `v` vs Quanto custa ir via `x`" (somando o custo do link direto com a estimativa do vizinho). Você documentaria isso através das atualizações de colunas na matriz correspondente de vizinhos vs. destinos.