package eu.kanade.mangafeed.presenter;

import android.content.Intent;

import javax.inject.Inject;

import eu.kanade.mangafeed.App;
import eu.kanade.mangafeed.data.helpers.DatabaseHelper;
import eu.kanade.mangafeed.data.helpers.PreferencesHelper;
import eu.kanade.mangafeed.data.models.Manga;
import eu.kanade.mangafeed.ui.activity.MangaDetailActivity;
import eu.kanade.mangafeed.ui.adapter.LibraryAdapter;
import eu.kanade.mangafeed.view.LibraryView;

import static rx.android.schedulers.AndroidSchedulers.mainThread;

public class LibraryPresenter extends BasePresenter {

    private LibraryView view;

    @Inject DatabaseHelper db;
    @Inject PreferencesHelper prefs;

    LibraryAdapter<Manga> adapter;

    public LibraryPresenter(LibraryView view) {
        this.view = view;
        App.getComponent(view.getActivity()).inject(this);

        //TODO remove, only for testing
        if (prefs.isFirstRun()) {
            db.manga.createDummyManga();
            db.chapter.createDummyChapters();
            prefs.setNotFirstRun();
        }

    }

    public void onMangaClick(int position) {
        Intent intent = MangaDetailActivity.newIntent(
                view.getActivity(),
                adapter.getItem(position)
        );
        view.getActivity().startActivity(intent);
    }

    public void initializeMangas() {
        adapter = new LibraryAdapter<>(view.getActivity());
        view.setAdapter(adapter);
        view.setMangaClickListener();

        subscriptions.add(db.manga.getWithUnread()
                        .observeOn(mainThread())
                        .subscribe(adapter::setNewItems)
        );
    }

    public void onQueryTextChange(String query) {
        adapter.getFilter().filter(query);
    }

}
