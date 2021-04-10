package com.mymdl.george.myymdll;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.mymdl.george.myymdll.R;

public class Info extends AppCompatActivity {

    private TextView mTextMessage;

    private String APropos = "Conditions générales d’utilisation de l’application mobile My MDL\n" +
            "DANS LEUR VERSION DU 15 AOÛT 2019\n" +
            "\n" +
            "Champ d’application des CGU :\n" +
            "Les présentes « conditions générales d'utilisation » ont pour objet l'encadrement juridique de l’utilisation de l’application My MDL et de ses services.\n" +
            " \n" +
            "Tout le long d’une utilisation de l’application par l’utilisateur, seule la version des Conditions Générales d’Utilisation en vigueur au moment de la dite utilisation est applicable\n" +
            " \n" +
            "Les responsables légaux d’un Utilisateur mineur sont tenus de veiller au respect des présentes Conditions Générales d’Utilisation par l’Utilisateur en question, ainsi que de l’utilisation faite par celui-ci des contenus proposés par l’application My MDL\n" +
            " \n" +
            "L’application My MDL se réserve le droit de modifier les clauses de ces Conditions Générales d’Utilisation à tout moment et sans justification. L’Utilisateur est tenu de s’informer régulièrement de l’évolution de ces Conditions Générales d’Utilisation.\n" +
            " \n" +
            " \n" +
            "ARTICLE 1 : Objet\n" +
            " \n" +
            " \n" +
            "Ce contrat est conclu entre :\n" +
            " \n" +
            "Le gérant du site internet, ci-après désigné « l’Éditeur »,\n" +
            " \n" +
            "Toute personne physique ou morale souhaitant télécharger l’application mobile My MDL, ci-après appelé « l’Utilisateur ».\n" +
            " \n" +
            "Les conditions générales d'utilisation doivent être acceptées par tout Utilisateur.\n" +
            " \n" +
            " \n" +
            "ARTICLE 2 : Mentions légales\n" +
            " \n" +
            "L’application My MDL est éditée par La Fédération des maisons des lycéen·ne·s ( la FMDL est une association loi 1901 à but non lucratif), dont le siège social est situé au 3/5 rue de Vincennes, 93100 Montreuil.\n" +
            " \n" +
            "L’association est représentée par Chloé Riazuelo (présidente)\n" +
            " \n" +
            " \n" +
            "ARTICLE 3 : accès aux services\n" +
            " \n" +
            "L’Utilisateur de l’application My MDL a accès aux services suivants :\n" +
            "Lire les informations relatives à un lycée donné proposées par l’application My MDL\n" +
            "Répondre aux sondages proposés par l’application My MDL sans garantie de connaissance des résultats\n" +
            " \n" +
            "Les services suivants ne sont accessibles pour l’Utilisateur que s’il est administrateur de niveau 1 (ou supérieur) de l’application (c’est-à-dire qu’il est identifié à l’aide de ses identifiants de connexion donnés par un administrateur de niveau 2) :\n" +
            "Poster des événements relatifs au Club qui lui a été attribué\n" +
            "Poster des sondages et accéder aux résultats\n" +
            "Modifier les contenus postés par lui-même\n" +
            " \n" +
            "Les services suivants ne sont accessibles pour l’Utilisateur que s’il est administrateur de niveau 2 (ou supérieur) de l’application (c’est-à-dire qu’il est identifié à l’aide de ses identifiants de connexion donnés par un administrateur de niveau 3) :\n" +
            "Créer et invalider des codes administrateurs de niveau 1\n" +
            "Modifier tous les événements et sondages postés par les administrateurs de niveau 1 du Lycée auquel il est rattaché\n" +
            "Accéder à l’espace statistique du Lycée auquel il est rattaché\n" +
            "Créer ou ajouter des Clubs sur le Lycée qui leur est rattaché\n" +
            "Accéder aux Messages envoyés par les administrateurs de niveau 3 et 4\n" +
            " \n" +
            "Les services suivants ne sont accessibles pour l’Utilisateur que s’il est administrateur de niveau 3 (ou supérieur) de l’application (c’est-à-dire qu’il est identifié à l’aide de ses identifiants de connexion donnés par un administrateur de niveau 4) :\n" +
            "Ajouter et supprimer des Lycées sur la Région à laquelle il est rattaché\n" +
            "Créer et invalider des codes administrateurs de niveau 2\n" +
            "Poster des messages à destination des administrateurs de niveau 2 de la Région à laquelle il est rattaché\n" +
            "Lire les messages envoyés par es administrateurs de niveau 4\n" +
            " \n" +
            "Les services suivants ne sont accessibles pour l’Utilisateur que s’il est administrateur de niveau 4 de l’application (c’est-à-dire qu’il est identifié à l’aide de ses identifiants de connexion décernés par l’Éditeur) :\n" +
            "Créer et invalider des codes administrateurs de niveau 3\n" +
            "Poster des messages à destination des administrateurs de niveau 3\n" +
            " \n" +
            "Les frais supportés par l’Utilisateur pour le bon fonctionnement de l’application My MDL (connexion internet, matériel informatique, etc.) ne sont pas à la charge de l’Éditeur.\n" +
            " \n" +
            "Les différents services de l’application My MDL peuvent être interrompus ou suspendus par l’Éditeur, sans obligation de préavis ou de justification.\n" +
            " \n" +
            " \n" +
            "ARTICLE 4 : Responsabilité de l’Utilisateur\n" +
            " \n" +
            "L’Utilisateur est responsable des risques liés à l’utilisation de son identifiant de connexion et de son mot de passe.\n" +
            " \n" +
            "Le mot de passe de l’Utilisateur doit rester secret. En cas de divulgation de mot de passe, l’Éditeur décline toute responsabilité.\n" +
            " \n" +
            "L’Utilisateur assume l’entière responsabilité de l’utilisation qu’il fait des informations et contenus sur l’application My MDL.\n" +
            " \n" +
            "Tout usage du service par l'Utilisateur ayant directement ou indirectement pour conséquence des dommages doit faire l'objet d'une indemnisation au profit de l’application.\n" +
            " \n" +
            "L’administrateur, de tout niveau, s’engage à tenir des propos respectueux des autres et de la loi et accepte que ces publications soient modérées ou refusées par l’Éditeur, sans obligation de justification.\n" +
            " \n" +
            "En publiant sur le site, l’Utilisateur cède à la FMDL le droit non exclusif et gratuit de représenter, reproduire, adapter, modifier, diffuser et distribuer sa publication, directement ou par un tiers autorisé.\n" +
            " \n" +
            "L’Éditeur s'engage toutefois à citer l’administrateur en cas d’utilisation de sa publication\n" +
            " \n" +
            " \n" +
            " \n" +
            "ARTICLE 5 : Responsabilité de l’Éditeur\n" +
            " \n" +
            "Tout dysfonctionnement du serveur ou du réseau ne peut engager la responsabilité de l’Éditeur.\n" +
            " \n" +
            "De même, la responsabilité de l’application ne peut être engagée en cas de force majeure ou du fait imprévisible et insurmontable d’un tiers.\n" +
            " \n" +
            "L’application My MDL s'engage à mettre en œuvre tous les moyens nécessaires pour garantir la sécurité et la confidentialité des données. Toutefois, il n’apporte pas une garantie de sécurité totale.\n" +
            " \n" +
            "L’Éditeur ne garantit pas la fiabilité des sources.\n" +
            " \n" +
            "ARTICLE 6 : Propriété intellectuelle\n" +
            " \n" +
            "Les contenus de l’application My MDL (logos, textes, éléments graphiques, vidéos, etc.) sont protégés par le droit d’auteur, en vertu du Code de la propriété intellectuelle.\n" +
            " \n" +
            "L’Utilisateur devra obtenir l’autorisation de l’Éditeur avant toute reproduction, copie ou publication de ces différents contenus.\n" +
            " \n" +
            "Ces derniers peuvent être utilisés par les utilisateurs uniquement à des fins privées : tout usage commercial est interdit.\n" +
            " \n" +
            "L’Utilisateur est entièrement responsable de tout contenu qu’il met en ligne et il s’engage à ne pas porter atteinte à un tiers.\n" +
            " \n" +
            "L’Éditeur de l’application My MDL se réserve le droit de modérer ou de supprimer librement et à tout moment les contenus mis en ligne par les utilisateurs, et ce sans justification.\n" +
            " \n" +
            " \n" +
            "ARTICLE 7 : Données personnelles\n" +
            " \n" +
            "L’application My MDL collecte les adresses électroniques (e-mail) administrateurs de niveau 1, 2, 3 et 4, les régions des administrateurs de niveau 3 ainsi que les lycées d’appartenance des administrateurs de niveau 1 et 2.\n" +
            " \n" +
            "L’adresse électronique sera notamment utilisée par l’application My MDL pour la communication d’informations diverses.\n" +
            " \n" +
            "Ces données personnelles sont les seules collectées par l’application My MDL\n" +
            " \n" +
            "Les données personnelles de l’utilisateur ne seront pas transmises à un tiers sauf si l’Éditeur y est contraint par la loi ou une décision de justice\n" +
            " \n" +
            "Les données personnelles de l’utilisateur sont conservées jusqu’à la fermeture de son compte\n" +
            " \n" +
            "Ces données sont conservées sur une plateforme sécurisée éditée par IBM\n" +
            " \n" +
            "En vertu de la loi « informatique et libertés » du 6 janvier 1978 modifiée et du règlement européen (UE 2016/679) relatif à la protection des personnes physiques à l’égard du traitement des données à caractère personnel et à la libre circulation des données (RGPD) l'Utilisateur dispose d'un droit d’accès, de rectification, de suppression et d’opposition de ses données personnelles. L'Utilisateur exerce ce droit par mail à appli@federation-mdl.fr\n" +
            " \n" +
            " \n" +
            "ARTICLE 8 : Liens hypertextes\n" +
            " \n" +
            "Les domaines vers lesquels mènent les liens hypertextes présents sur l’application My MDL n’engagent pas la responsabilité de l’Éditeur de l’application My MDL, qui n’a pas de contrôle sur ces liens\n" +
            " \n" +
            " \n" +
            "ARTICLE 9 : Durée du contrat\n" +
            " \n" +
            "La durée du présent contrat est indéterminée. Le contrat produit ses effets à l'égard de l'Utilisateur à compter du début de l’utilisation du service.\n" +
            " \n" +
            " \n" +
            "ARTICLE 10 : Droit applicable et juridiction compétente\n" +
            " \n" +
            "Le présent contrat dépend de la législation française. \n",
    Contact = "Contact : \n\n  Pour signaler " +
            "un problème dans l'application ou autre : \n\n vincent.ky09@gmail.com / theomanea9@gmail.com /  " +
            "juliendegentile@lilo.org \n\n MyMDL v 1.0",
    Credit = "Crédits : \n\n Nous remercions pour cette application  Julien De Gentille les deux développeurs " +
            "Théo Manea, et Vincent Ky et la FMDL pour son soutien.";

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(APropos);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(Contact);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(Credit);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mTextMessage.setText(APropos);
    }

}
