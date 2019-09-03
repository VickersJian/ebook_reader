/* Created by Vickers Jian on 2019/08 */
package com.vickers.ebook_reader.Base;

/**
 * Result 泛型类，用于储存得到的所需结果(Success)<p>
 * 或未得到所需结果的错误信息(Error)
 *
 * @param <T> 仅用于说明所需保存结果的类型
 */
public class Result<T> {
    //隐藏构造函数，限制使用子类类型（只能用Success或Error）
    private Result() {
    }

    /**
     * 重写Object的toString方法，返回详细的错误信息
     */
    @Override
    public String toString() {
        if (this instanceof Result.Success) {
            Result.Success success = (Result.Success) this;
            return "Success[data=" + success.getData().toString() + "]";
        } else if (this instanceof Result.Error) {
            Result.Error error = (Result.Error) this;
            return "Error[exception=" + error.getError().toString() + "]";
        }
        return "";
    }

    /**
     * Success类，储存已得到的所需结果
     *
     * @param <E> 保存结果的类型
     */
    public final static class Success<E> extends Result {
        private E data;

        public Success(E data) {
            this.data = data;
        }

        public E getData() {
            return this.data;
        }
    }

    /**
     * Error类,储存未得到所需结果的错误信息
     */
    public final static class Error extends Result {
        private Exception error;
        /**
         * @param error 错误信息
         **/
        public Error(Exception error) {
            this.error = error;
        }

        public Exception getError() {
            return this.error;
        }
    }
}
