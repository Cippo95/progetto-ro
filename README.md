# Progetto Ricerca Operativa - Filippo Landi

Devo risolvere il progetto n.53 fatto per una persona: **Single machine scheduling, weighted completion times with precedence constraints**.  

Ho scritto il codice per risolvere il problema in Java.  

### Usare il programma

Per usare il programma generando dei job con precedenze (non cicliche) casuali:    
`java JobMain -j [NUMERO]`  
Dove NUMERO è il numero dei job.  

Per usare il programma caricando da file csv i job e le precedenze (guardare jobs.csv per un esempio):  
`java JobMain -f [FILE]`      
Dove FILE è il path a un file csv.  

Per mostrare a terminale cosa succede ad ogni turno di computazione:    
`java JobMain [OPZIONI] -debug`  

### Documentazione

Per maggiori informazioni sull'implementazione leggere docs/documentazione.pdf
