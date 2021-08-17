export const formatPrice = (price: number) => {
    const params = { maximumFractionDigits: 2, minimunFractionDigits: 2};
    return new Intl.NumberFormat('pt-BR', params).format(price);
}