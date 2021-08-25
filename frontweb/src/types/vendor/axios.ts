import { Method } from "axios";

export type AxioParams = {
    method?: Method;
    url: string;
    data?: object;
    params?: object;
}