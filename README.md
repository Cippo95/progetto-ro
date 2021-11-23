# progetto-ro
Progetto Ricerca Operativa - Filippo Landi

Devo risolvere il progetto n.53 fatto per una persona: **Single machine scheduling, weighted completion times with precedence constraints**.

Descrizione del problema: 

*Sono dati n jobs J={j1,..,jn} di cui è noto per ciascuno il tempo di esecuzione d^j e il peso w^j (non correlato con d^j). Inoltre esistono delle regole di precedenza fra coppie di jobs descritte da un grafo aciclico. Il tempo di completamento del job j, C^j è definito come l'istante di fine lavorazione del job. Ogni job, una volta iniziata la lavorazione, va portato a termine senza interruzioni. Si determini la sequenza di esecuzione dei jobs sull'unica macchina disponibile che minimizza la somma pesata dei tempi di completamento dei jobs nel rispetto delle precedenze.*

Idee prima di iniziare: 
- Dovrò generare casualmente:
	- I job che hanno:
		1. Un tempo di esecuzione;
		2. Un peso, immagino come una priorità, maggiore il peso maggiore priorità;
	- Un ciclo aciclico di precedenze temporali per questi job.
- Ripassare il pdf delle euristiche greedy dove ci sono problemi simili.
- Probabilmente userò il linguaggio java, anche se non il più efficiente ma l'ho appena ripassato quindi lo ho fresco.

Realizzato al momento un programma Java basic:
- Job generati casualmente, numero di essi passato come argomento al programma.
- Realizzata la greedy che prende per il rapporto durata/valore non decrescente più la gestione delle dipendenze.

Commenti generali:
- Programma principale è il JobMain. DagGenerator è una vecchia prova da rimuovere poi.
- Per ora durata e valore dei job sono interi come nelle lezioni ma possono essere cambiati in float/double.
- Il rapporto durata/valore è un double.
- Politica delle precedenze è un grafo acicilico (in caso contrario avremmo dei deadlock):
	- Realizzato tramite una matrice triangolare inferiore di dimensione data dal numero dei job:
		1. Se un job (riga) aspetta un altro (colonna) il valore immesso è 1 in caso contrario 0.
		2. Un job non aspetta se stesso per cui 0 su tutta la diagonale.
- Codice forse più ottimizzabile in diversi punti.

Aggiunte che voglio fare:
- [x] Argomento per abilitare o disabilitare le stampe a schermo che di sicuro rallentano il programma.
- [ ] Inserimento tramite un file di testo (interattivo non mi piace, scrivo una volta): da capire jaxb.
- [x] Inserire controllo precedenze, credo a runtime: termino con esito negativo se trovo che non ci sono job candidati (grado 0).
- [ ] Rappresentazione delle precedenze tramite linguaggio dot che permette una rappresentazione grafica da fare prima del run così anche ad occhio si possono vedere eventuali cicli.
- [ ] Modello matematico del problema.
