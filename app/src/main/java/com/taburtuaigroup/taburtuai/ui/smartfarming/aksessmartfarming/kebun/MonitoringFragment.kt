package com.taburtuaigroup.taburtuai.ui.smartfarming.aksessmartfarming.kebun

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.taburtuaigroup.taburtuai.R
import com.taburtuaigroup.taburtuai.ViewModelFactory
import com.taburtuaigroup.taburtuai.data.DailyWeather
import com.taburtuaigroup.taburtuai.data.Mdate
import com.taburtuaigroup.taburtuai.data.MonitoringKebun
import com.taburtuaigroup.taburtuai.data.WeatherTime
import com.taburtuaigroup.taburtuai.databinding.FragmentMonitoringBinding
import com.taburtuaigroup.taburtuai.util.DataConverter
import com.taburtuaigroup.taburtuai.util.TextFormater
import com.taburtuaigroup.taburtuai.util.ToastUtil
import java.util.*


class MonitoringFragment : Fragment() {

    private var _binding: FragmentMonitoringBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: KebunViewModel
    private lateinit var weatherForcastAdapter:WeatherForcastAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModel = ViewModelProvider(
            requireActivity(),
            ViewModelFactory.getInstance(requireActivity().application)
        )[KebunViewModel::class.java]

        val layoutManager = LinearLayoutManager(requireActivity())
        binding.rvWeatherForcast.layoutManager = layoutManager

        weatherForcastAdapter = WeatherForcastAdapter()
        binding.rvWeatherForcast.adapter = weatherForcastAdapter

        viewModel.monitoring.observe(requireActivity()) {
            if(isAdded){
                if (it != null) {
                    showData(it)
                } /*else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.tidak_ada_data_monitoring),
                        Toast.LENGTH_SHORT
                    ).show()
                    //TODO show no data
                }*/
            }
        }

        viewModel.weatherForcast.observe(requireActivity()){
            it?.dailyWeather?.let { it1 ->
                weatherForcastAdapter.submitList(it1)
                showCurrentWeather(it.dailyWeather)
            }
        }

        viewModel.message.observe(requireActivity()){
            if(!it.first) {
                it.second.getContentIfNotHandled()
            }else{
                ToastUtil.makeToast(requireActivity(),it)
            }
            showWeatherForcast(!it.first)
        }

    }

    private fun showCurrentWeather(list:List<DailyWeather>){
        for(i in list){
            val rightNow = Calendar.getInstance()
            val year=rightNow.get(Calendar.YEAR)
            val month=rightNow.get(Calendar.MONTH)+1
            val day=rightNow.get(Calendar.DAY_OF_MONTH)
            val hour =rightNow.get(Calendar.HOUR_OF_DAY)
            val currentTime= Mdate(year,month,day,null)
            var weatherTime=WeatherTime()
            var time=""
            if(i.date==currentTime){
                if(hour<6){
                    time=getString(R.string.dini_hari)
                    weatherTime=i.diniHari
                }else if(hour<12){
                    time=getString(R.string.pagi)
                    weatherTime=i.pagiHari
                }else if(hour<18){
                    time=getString(R.string.siang)
                    weatherTime=i.siangHari
                }else if(hour<24){
                    time=getString(R.string.malam)
                    weatherTime=i.malamHari
                }
                binding.iconWeather.setImageDrawable(DataConverter.getWeatherDrawable(weatherTime.weatherCode,requireActivity()))
                binding.tvCurrentTime.text=time
                binding.tvCurrentDate.text="${i.date.date}/${i.date.month}"
                binding.tvWeatherName.text=TextFormater.getWeatherName(weatherTime.weatherCode,requireActivity())
                binding.tvHumidity.text=weatherTime.humidity
                binding.tvTemp.text=weatherTime.temperature.replace("C","℃")
                binding.tvWindDirection.text=DataConverter.getWindDirection(weatherTime.windDirection,requireActivity())
                binding.tvWindSpeed.text=weatherTime.windSpeed+" Km/H"
                break
            }
        }
    }

    override fun onStart() {
        super.onStart()
        binding.cvPerkiraanCuaca.visibility=View.GONE
        binding.tvPerkiraanCuacaLabel.visibility=View.GONE
    }
    private fun showWeatherForcast(isShow:Boolean){
        if(isShow){
            /*val text = ObjectAnimator.ofFloat(binding.tvPerkiraanCuacaLabel, View.ALPHA, 1f).setDuration(500)
            val cv = ObjectAnimator.ofFloat(binding.cvPerkiraanCuaca, View.ALPHA, 1f).setDuration(500)
            val s=AnimatorSet()
            s.apply {
                playTogether(text, cv)
                start()
            }*/
            binding.tvPerkiraanCuacaLabel.visibility=View.VISIBLE
            binding.cvPerkiraanCuaca.visibility=View.VISIBLE
        }else{
            binding.tvPerkiraanCuacaLabel.visibility=View.GONE
            binding.cvPerkiraanCuaca.visibility=View.GONE
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showData(data: MonitoringKebun) {
        binding.apply {
            tvTemperature.text = (data.temperatur ?:"-").toString()+"\u2103"
            tvKelembaban.text = (data.kelembaban_tanah ?:"-").toString()+" RH"
            tvPhTanah.text = "pH "+(data.ph_tanah ?:"-").toString()
            tvKelembabanUdara.text = (data.humidity ?:"-").toString()+" g/"+"\u33A5"
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