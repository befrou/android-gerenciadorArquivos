package dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import utils.RunTimePermission;

import com.example.bruno.gerenciadordearquivos.MainActivity;
import com.example.bruno.gerenciadordearquivos.R;

import java.io.File;

public class NovaPastaDialog extends DialogFragment{
    private static String path;

    @SuppressLint("ValidFragment")
    public NovaPastaDialog(String path) {
        this();
        this.path = path;
    }

    public NovaPastaDialog() {super();}

    public static interface OnCompleteListener {
        public abstract void onComplete();
    }

    private OnCompleteListener mListener;

    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            this.mListener = (OnCompleteListener)context;
        }
        catch (final ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnCompleteListener");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mListener.onComplete();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Activity activity = getActivity();

        LayoutInflater inflater = activity.getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_nova_pasta, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Nova Pasta")
                .setView(v)
                .setPositiveButton("Criar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText txtNovaPasta = ((Dialog)dialog).findViewById(R.id.txtNovaPasta);
                        String delimiter = (NovaPastaDialog.path.equals("/")) ? "" : "/";

                        String novaPasta = Environment.getExternalStorageDirectory().getPath() + NovaPastaDialog.path + delimiter + txtNovaPasta.getText();

                        if(new File(novaPasta).mkdirs()) {
                            Toast.makeText(activity.getBaseContext(), "Pasta criada com sucesso!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(activity.getBaseContext(), "Não foi possível criar a pasta!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        return builder.create();
    }
}
