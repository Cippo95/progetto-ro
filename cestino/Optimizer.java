public Optimizer {
	int Greedy(List<Job> job, int[][] matrix, boolean debug) {
		//variabili
		int jobsNumber = job.size();
		int [] priorities = new int[jobsNumber];
		int jobPrio = 0;
		indexes = new int[jobsNumber];
		
		System.out.println("Lavori assegnati:");
		for (int i = 0; i < jobsNumber); i++) {
			System.out.println(jobs.get(i));
		}
		
		System.out.println();
		System.out.println("Matrice delle precedenze");
		//più leggibile stampata per righe
		for (int i = 0; i < jobsNumber; i++) {
			System.out.println(Arrays.toString(matrix[i]));
		}

		//CALCOLO LE PRIORITÀ DEI JOB

		for (int i = 0; i < jobsNumber; i++) {
			for (int j = 0; j < jobsNumber; j++) {
				jobPrio += matrix[i][j];
			}
			priorities[i] = jobPrio;
			jobPrio = 0;
		}
		System.out.println();
		System.out.println("Grado di priorità iniziale:");
		System.out.println(Arrays.toString(priorities));
		//aggiungo nuovi job grado 0 e parte algoritmo principale
		for (int k = 0; k < jobsNumber; k++) {
			if (priorities[k] == 0
				&& !readyJobs.contains(jobs.get(k))) {
				readyJobs.add(jobs.get(k));
			}
		}
		for (int i = 0; i < jobsNumber; i++) {
			if (debug) {
				System.out.println();
				System.out.println("Turno numero: " + i);
				System.out.println("Job candidati:");
				System.out.println(readyJobs);
			}
			if (readyJobs.size() == 0) {
				System.err.println("Errore di precedenze: "
				+ "non ci sono job candidati (deadlock)");
				return;
			}
			if (debug) {
				System.out.println("Best job: ");
				System.out.println(Collections.min(readyJobs));
			}
			//job migliore, lo tolgo dai ready, prio -1 lo esclude
			indexes[i] = Collections.min(readyJobs).getIndex();
			readyJobs.remove(Collections.min(readyJobs));
			priorities[indexes[i]]--;
			for (int j = 0; j < jobsNumber; j++) {
				//diminuisco grado a chi aspettava il job
				if(matrix[j][indexes[i]] == 1) {
					priorities[j]--;
				}
				//aggiungo job grado 0, se nuovo
				if (priorities[j] == 0
					&& !readyJobs.contains(jobs.get(j))) {
					readyJobs.add(jobs.get(j));
				}
			} 
			if (debug) {
				System.out.println("Priorità aggiornate:");
				System.out
				.println(Arrays.toString(priorities));
			}	

		}
		System.out.println();
		System.out.println("Ordine di esecuzione:");
		if (fileLoad) {
			String[] indexesToNames = new String[jobsNumber];
			for (int i = 0; i < jobsNumber; i++) {
				indexesToNames[i] = jobsNames.get(indexes[i]);
			}
			System.out.println(Arrays.toString(indexesToNames));
		} else {
			System.out.println(Arrays.toString(indexes));
		}
		System.out.println();
		System.out.println("Tempi di attesa per job");
		int wait = 0;
		int tw = 0;
		int[] waitingTime = new int[jobsNumber];
		for (int i = 0; i < jobsNumber; i++) {
			wait += jobs.get(indexes[i]).getSpan();
			waitingTime[i] = wait;
			tw += wait * jobs.get(indexes[i]).getValue();
		}
		System.out.println(Arrays.toString(waitingTime));
		System.out.println();
		System.out.println("Somma pesata tempi completamento jobs: " + tw);
	}
}
		
