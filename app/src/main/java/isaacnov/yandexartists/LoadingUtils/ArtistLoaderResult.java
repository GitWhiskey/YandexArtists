package isaacnov.yandexartists.LoadingUtils;

/**
 * Результат загрузки списка испонителей. Содержит пару значений: сообщение - адаптер.
 * При успешном выполнении загрузки и парсинга файла JSON, message будет равно "Success",
 * в ином случае - будет содержать причину сбоя работы. ArtistArrayAdapter при ошибке будет
 * равен null.
 *
 *  @author Max K.
 */
public class ArtistLoaderResult {

    private String message;
    private ArtistsArrayAdapter adapter;

    public ArtistLoaderResult(String message, ArtistsArrayAdapter adapter) {
        this.message = message;
        this.adapter = adapter;
    }

    public String getMessage() {
        return message;
    }

    public ArtistsArrayAdapter getAdapter() {
        return adapter;
    }
}
