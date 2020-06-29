package com.mad_devs.fitnesstrackerd.bluetooth.view;

public interface ListInteractionListener<T> {
    /**
     * Вызывается при нажатии элемента списка.
     *
     * @param item выбранный элемент.
     */
    void onItemClick(T item);

    /**
     * Вызывается при получении элементов списка.
     */
    void startLoading();

    /**
     * CВызывается, когда один или все элементы списка были выбраны.
     *
     * @param partialResults Значение true, если результаты являются частичными, а
     *                       * выборка продолжается, в противном случае - значение false.
     */
    void endLoading(boolean partialResults);

    /**
     * Вызывается, чтобы закрыть диалог загрузки.
     *
     * @param error   true, если произошла ошибка, иначе false.
     * @param element элемент списка обработан.
     */
    void endLoadingWithDialog(boolean error, T element);

}
