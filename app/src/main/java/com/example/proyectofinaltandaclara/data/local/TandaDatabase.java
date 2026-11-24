package com.example.proyectofinaltandaclara.data.local;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import com.example.proyectofinaltandaclara.data.model.Pago;
import com.example.proyectofinaltandaclara.data.model.Participante;
import com.example.proyectofinaltandaclara.data.model.Tanda;

/**
 * Base de datos SQLite gestionada por Room para la app TandaClara.
 */
@Database(
    entities = {Tanda.class, Participante.class, Pago.class},
    version = 1,
    exportSchema = false
)
@TypeConverters({Converters.class})
public abstract class TandaDatabase extends RoomDatabase {

    public abstract TandaDao tandaDao();
    public abstract ParticipanteDao participanteDao();
    public abstract PagoDao pagoDao();

    private static volatile TandaDatabase INSTANCE;

    public static TandaDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            synchronized (TandaDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.getApplicationContext(),
                        TandaDatabase.class,
                        "tanda_clara_database"
                    ).fallbackToDestructiveMigration().build();
                }
            }
        }
        return INSTANCE;
    }
}
