# Progetto Ricerca Operativa - Filippo Landi

Devo risolvere il progetto n.53 fatto per una persona: **Single machine scheduling, weighted completion times with precedence constraints**.  

Ho scritto il codice per fornire una soluzione ammissibile in Java.  

### Usare il programma (NEW)

Ho implementato un algoritmo genetico che si può decidere di lanciare dopo aver visto la soluzione greedy.
Attenzione però ai tempi di esecuzione con l'algoritmo genetico: consiglio di partire con 10 lavori.
Per fare un esempio il mio pc arrivato sui 40-50 incomincia a metterci qualche minuto a completare la computazione.

Per usare il programma generando dei job con precedenze (non cicliche) casuali:    
`java JobMainGA -j [NUMERO]`  
Dove NUMERO è il numero dei job.  

Per usare il programma caricando da file csv i job e le precedenze (guardare jobs.csv per un esempio):  
`java JobMainGA -f [FILE]`      
Dove FILE è il path a un file csv.  

Per mostrare a terminale cosa succede ad ogni turno di computazione (mostra informazioni solo per la greedy):    
`java JobMainGA [OPZIONE -j o -f] -debug`

### Usare il programma (OLD)

Per usare il programma generando dei job con precedenze (non cicliche) casuali:    
`java JobMain -j [NUMERO]`  
Dove NUMERO è il numero dei job.  

Per usare il programma caricando da file csv i job e le precedenze (guardare jobs.csv per un esempio):  
`java JobMain -f [FILE]`      
Dove FILE è il path a un file csv.  

Per mostrare a terminale cosa succede ad ogni turno di computazione:    
`java JobMain [OPZIONE -j o -f] -debug`  

### Documentazione

Per maggiori informazioni sull'implementazione leggere docs/documentazione.pdf
