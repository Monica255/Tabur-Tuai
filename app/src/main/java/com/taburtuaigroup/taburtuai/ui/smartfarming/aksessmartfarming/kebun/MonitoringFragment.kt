package com.taburtuaigroup.taburtuai.ui.smartfarming.aksessmartfarming.kebun

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.taburtuaigroup.taburtuai.R
import com.taburtuaigroup.taburtuai.core.domain.model.DailyWeather
import com.taburtuaigroup.taburtuai.core.domain.model.Mdate
import com.taburtuaigroup.taburtuai.core.domain.model.MonitoringKebun
import com.taburtuaigroup.taburtuai.core.domain.model.WeatherTime
import com.taburtuaigroup.taburtuai.databinding.FragmentMonitoringBinding
import com.taburtuaigroup.taburtuai.core.util.DataConverter
import com.taburtuaigroup.taburtuai.core.util.TextFormater
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class MonitoringFragment : Fragment() {

    private var _binding: FragmentMonitoringBinding? = null
    private val binding get() = _binding!!
    private val viewModel: KebunViewModel by activityViewModels()
    private lateinit var weatherForcastAdapter: WeatherForcastAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = LinearLayoutManager(requireActivity())
        binding.rvWeatherForcast.layoutManager = layoutManager

        weatherForcastAdapter = WeatherForcastAdapter()
        binding.rvWeatherForcast.adapter = weatherForcastAdapter

        viewModel.monitoring.observe(requireActivity()) {
            if (isAdded) {
                showData(it)
            }
        }

        viewModel.weatherForcastData.observe(requireActivity()) {
            if (it.isNotEmpty()) {
                weatherForcastAdapter.submitList(it)
                showCurrentWeather(it)
                showWeatherForcast(true)
            } else showWeatherForcast(false)

        }
    }

    private fun showCurrentWeather(list: List<DailyWeather>) {
        for (i in list) {
            val rightNow = Calendar.getInstance()
            val year = rightNow.get(Calendar.YEAR)
            val month = rightNow.get(Calendar.MONTH) + 1
            val day = rightNow.get(Calendar.DAY_OF_MONTH)
            val hour = rightNow.get(Calendar.HOUR_OF_DAY)
            val currentTime = Mdate(year, month, day, null)
            var weatherTime = WeatherTime()
            var time = ""
            if (i.date == currentTime) {
                if (hour < 6) {
                    time = getString(R.string.dini_hari)
                    weatherTime = i.diniHari
                } else if (hour < 12) {
                    time = getString(R.string.pagi)
                    weatherTime = i.pagiHari
                } else if (hour < 18) {
                    time = getString(R.string.siang)
                    weatherTime = i.siangHari
                } else if (hour < 24) {
                    time = getString(R.string.malam)
                    weatherTime = i.malamHari
                }
                binding.iconWeather.setImageDrawable(
                    DataConverter.getWeatherDrawable(
                        weatherTime.weatherCode,
                        requireActivity()
                    )
                )
                binding.tvCurrentTime.text = time
                binding.tvCurrentDate.text = "${i.date.date}/${i.date.month}"
                binding.tvWeatherName.text =
                    TextFormater.getWeatherName(weatherTime.weatherCode, requireActivity())
                binding.tvHumidity.text = weatherTime.humidity
                binding.tvTemp.text = weatherTime.temperature.replace("C", "℃")
                binding.tvWindDirection.text =
                    DataConverter.getWindDirection(weatherTime.windDirection, requireActivity())
                binding.tvWindSpeed.text = weatherTime.windSpeed + " Km/H"
                break
            }
        }
    }

    override fun onStart() {
        super.onStart()
        binding.cvPerkiraanCuaca.visibility = View.GONE
        binding.tvPerkiraanCuacaLabel.visibility = View.GONE
    }

    private fun showWeatherForcast(isShow: Boolean) {
        if (isShow) {
            /*val text = ObjectAnimator.ofFloat(binding.tvPerkiraanCuacaLabel, View.ALPHA, 1f).setDuration(500)
            val cv = ObjectAnimator.ofFloat(binding.cvPerkiraanCuaca, View.ALPHA, 1f).setDuration(500)
            val s=AnimatorSet()
            s.apply {
                playTogether(text, cv)
                start()
            }*/
            binding.tvPerkiraanCuacaLabel.visibility = View.VISIBLE
            binding.cvPerkiraanCuaca.visibility = View.VISIBLE
        } else {
            binding.tvPerkiraanCuacaLabel.visibility = View.GONE
            binding.cvPerkiraanCuaca.visibility = View.GONE
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showData(data: MonitoringKebun) {
        binding.apply {
            tvTemperature.text = (data.temperatur ?: "-").toString() + "\u2103"
            tvKelembaban.text = (data.kelembaban_tanah ?: "-").toString() + " RH"
            tvPhTanah.text = "pH " + (data.ph_tanah ?: "-").toString()
            tvKelembabanUdara.text = (data.humidity ?: "-").toString() + " g/" + "\u33A5"

            cvTemperature.visibility= if(data.temperatur==null) View.GONE else View.VISIBLE
            cvKelembaban.visibility= if(data.kelembaban_tanah==null) View.GONE else View.VISIBLE
            cvPhTanah.visibility= if(data.ph_tanah==null) View.GONE else View.VISIBLE
            cvKelembabanUdara.visibility= if(data.humidity==null) View.GONE else View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMonitoringBinding.inflate(inflater, container, false)
        return binding.root
    }
}