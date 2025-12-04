package Replacement;

import Model.PageTable;

public interface ReplacementStrategy {

    /**
     * Alege numărul paginii care va fi inlocuita.
     * @param pageTable tabela de pagini (pentru informații: present, lastUsedTime, loadedTime etc.)
     * @return pageNumber al paginii victime.
     */
    int chooseVictim(PageTable pageTable);

}
