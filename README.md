# progetto-ro
Progetto Ricerca Operativa - Filippo Landi

Devo risolvere il progetto n.53 fatto per una persona: **Single machine scheduling, weighted completion times with precedence constraints**.

Descrizione del problema: 

*Sono dati n jobs J={j1,..,jn} di cui è noto per ciascuno il tempo di esecuzione d^j e il peso w^j (non correlato con d^j). Inoltre esistono delle regole di precedenza fra coppie di jobs descritte da un grafo aciclico. Il tempo di completamento del job j, C^j è definito come l'istante di fine lavorazione del job. Ogni job, una volta iniziata la lavorazione, va portato a termine senza interruzioni. Si determini la sequenza di esecuzione dei jobs sull'unica macchina disponibile che minimizza la somma pesata dei tempi di completamento dei jobs nel rispetto delle precedenze.*

Idee al momento: 
- Dovrò generare casualmente:
	- I job che hanno:
		1. Un tempo di esecuzione;
		2. Un peso, immagino come una priorità, maggiore il peso maggiore priorità;
	- Un ciclo aciclico di precedenze temporali per questi job.
- Ripassare il pdf delle euristiche greedy dove ci sono problemi simili.
- Probabilmente userò il linguaggio java, anche se non il più efficiente ma l'ho appena ripassato quindi lo ho fresco.

