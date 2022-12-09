package com.example.outworking.Compteur;

/**
 * Interface intervenant dans le mécanisme d'abonnement auditeur/source
 * En association avec la classe UpdateSource
 *
 */
public interface OnUpdateListener {

    // Méthode appelée à chaque update de l'objet de type UpdateSource (après abonnement)
    public void onUpdate();

    // Méthode appelée dés que une activitée est changé
    public void onUpdateActivity();

    // Méthode appelée quand le timer est lancé ou stoppé
    public void onStatusChange();

    // Méthode appelée quand une activitée est finis
    public void onActivityFinish();

    // Méthode appelée quand toute la derniére activitée est finis
    public void onFinish();
}

